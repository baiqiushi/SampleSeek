package edu.uci.ics.cloudberry.sampleseek.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.uci.ics.cloudberry.sampleseek.model.Operator;
import edu.uci.ics.cloudberry.sampleseek.model.Query;

import java.lang.reflect.Array;
import java.sql.Timestamp;

public class QueryExecutor {

    private final int outputSize = 10;

    private SampleManager sampleManager;

    public QueryExecutor(SampleManager sampleManager) {
        this.sampleManager = sampleManager;
    }

    public JsonNode executeQuery (Query query) {
        System.out.println("Executing query: \n" + query);
        long start = System.currentTimeMillis();
        ObjectNode result = JsonNodeFactory.instance.objectNode();

        // no group by queries
        if (query.getGroupBy() == null) {

        }
        // group by queries
        else {
            System.out.println("group by queries are not supported yet. return ...");
            return result;
        }

        // scan the whole sample table and return records meeting filter conditions
        ArrayNode resultArray = result.putArray("result");
        for (int i = 0; i < sampleManager.getSampleTableSize(); i ++) {
            if (tupleHit(query, i)) {
                // project values for SELECT attributes
                ArrayNode resultTuple = JsonNodeFactory.instance.arrayNode();
                for (int j = 0; j < query.getSelect().length; j ++) {
                    String attribute = query.getSelect()[j];
                    Object columnStore = sampleManager.getSample().get(attribute);
                    switch (sampleManager.getSampleTableColumnType(attribute).toLowerCase()) {
                        case "int":
                        case "integer":
                        case "number":
                            resultTuple.add((int)Array.get(columnStore, i));
                            break;
                        case "bigint":
                            resultTuple.add((long)Array.get(columnStore, i));
                            break;
                        case "double":
                            resultTuple.add((double)Array.get(columnStore, i));
                            break;
                        default:
                            resultTuple.add(String.valueOf(Array.get(columnStore, i)));
                            break;
                    }
                }
                resultArray.add(resultTuple);
            }
        }
        result.put("length", resultArray.size());

        long end = System.currentTimeMillis();
        System.out.println("Executing query DONE!  Time: " + String.format("%.3f", (end - start)/1000.0) + " seconds");

        // output sample result
        System.out.println("=== Query result ===");
        System.out.println("{\"length\": " + resultArray.size() + ", \"result\": [");
        for (int i = 0; i < Math.min(outputSize, resultArray.size()); i ++) {
            System.out.println(resultArray.get(i));
        }
        System.out.println("... ...");
        System.out.println("(total " + resultArray.size() + " lines)");

        return result;
    }

    private boolean tupleHit(Query query, int i) {
        boolean hit = true;

        // for each filter
        for (int j = 0; j < query.getFilters().length; j ++) {
            String attribute = query.getFilters()[j].getAttribute();
            Operator operator = query.getFilters()[j].getOperator();
            String[] operands = query.getFilters()[j].getOperands();

            Object columnStore = sampleManager.getSample().get(attribute);
            String columnType = sampleManager.getSampleTableColumnType(attribute);

            switch (columnType.toLowerCase()) {
                case "int":
                case "integer":
                case "number":
                    switch (operator) {
                        case IN:
                            hit = new ExpressionEvaluator<Integer>()
                                    .evaluateDoubleOperand(Array.getInt(columnStore, i),
                                            operator, Integer.valueOf(operands[0]), Integer.valueOf(operands[1]));
                            break;
                        default:
                            hit = new ExpressionEvaluator<Integer>()
                                    .evaluateSingleOperand(Array.getInt(columnStore, i),
                                            operator, Integer.valueOf(operands[0]));
                    }
                    break;
                case "bigint":
                    switch (operator) {
                        case IN:
                            hit = new ExpressionEvaluator<Long>()
                                    .evaluateDoubleOperand(Array.getLong(columnStore, i),
                                            operator, Long.valueOf(operands[0]), Long.valueOf(operands[1]));
                            break;
                        default:
                            hit = new ExpressionEvaluator<Long>()
                                    .evaluateSingleOperand(Array.getLong(columnStore, i),
                                            operator, Long.valueOf(operands[0]));
                    }
                    break;
                case "double":
                    switch (operator) {
                        case IN:
                            hit = new ExpressionEvaluator<Double>()
                                    .evaluateDoubleOperand(Array.getDouble(columnStore, i),
                                            operator, Double.valueOf(operands[0]), Double.valueOf(operands[1]));
                            break;
                        default:
                            hit = new ExpressionEvaluator<Double>()
                                    .evaluateSingleOperand(Array.getDouble(columnStore, i),
                                            operator, Double.valueOf(operands[0]));
                    }
                    break;
                case "timestamp":
                    Timestamp valueTime = (Timestamp) Array.get(columnStore, i);
                    Timestamp leftTime = Timestamp.valueOf(operands[0]);
                    switch (operator) {
                        case IN:
                            Timestamp rightTime = Timestamp.valueOf(operands[1]);
                            if (valueTime.after(rightTime) || valueTime.before(leftTime)) {
                                hit = false;
                            }
                            break;
                        case LT:
                            if (valueTime.after(leftTime)) {
                                hit = false;
                            }
                            break;
                        case GT:
                            if (valueTime.before(leftTime)) {
                                hit = false;
                            }
                            break;
                        case EQUAL:
                            if (!valueTime.equals(leftTime)) {
                                hit = false;
                            }
                            break;
                    }
                    break;
                default:
                    String valueString = (String) Array.get(columnStore, i);
                    String leftString = operands[0];
                    switch (operator) {
                        case IN:
                            String rightString = operands[1];
                            if (valueString.compareTo(leftString) < 0 || valueString.compareTo(rightString) > 0) {
                                hit = false;
                            }
                            break;
                        case LT:
                            if (valueString.compareTo(leftString) > 0) {
                                hit = false;
                            }
                            break;
                        case GT:
                            if (valueString.compareTo(leftString) < 0) {
                                hit = false;
                            }
                            break;
                        case EQUAL:
                            if (!valueString.equals(leftString)) {
                                hit = false;
                            }
                            break;
                    }
                    break;
            }
        }
        return hit;
    }

    public class ExpressionEvaluator<T extends Number & Comparable<? super T>> {
        public boolean evaluateSingleOperand(T value, Operator operator, T operand) {
            switch (operator) {
                case LT:
                    if (value.compareTo(operand) <= 0) {
                        return true;
                    }
                case GT:
                    if (value.compareTo(operand) >= 0) {
                        return true;
                    }
                case EQUAL:
                    if (value.compareTo(operand) == 0) {
                        return true;
                    }
            }
            return false;
        }

        public boolean evaluateDoubleOperand(T value, Operator operator, T left, T right) {
            switch (operator) {
                case IN:
                    if (value.compareTo(left) >= 0 && value.compareTo(right) <= 0) {
                        return true;
                    }
            }
            return false;
        }
    }
}
