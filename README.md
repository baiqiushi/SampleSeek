# Java implementation of Sample+Seek*
* *[Sample+Seek is a system published by Bolin Ding, etc. @Microsoft in SIMOD 2016](https://www.microsoft.com/en-us/research/publication/sample-seek-approximating-aggregates-with-distribution-precision-guarantee/)

## Current support
 - Restful API for query submission
 - Uniform sampling based queries
 - Non-group-by queries
 - Configuarable base table and sample table schemas
 
## Run the system

### Prerequisite
 - Java 8
 - Maven
 - PostgreSQL
 
### Build
```bash
mvn clean package
```
### Configuration
Modify the `sampleseek.yaml` file according to your settings.

### Run
```bash
java -jar target/cloudberry-1.0-SNAPSHOT-allinone.jar -c src/sampleseek.yaml
```

### Send query
```bash
curl -H "Content-Type: application/json" -X POST -d '{"select": ["x", "y"], "filters": [{"attribute": "create_at", "operator": "IN", "operands": ["2017-06-01 00:00:00", "2017-07-01 00:00:00"]}]}' http://localhost:8080/query
```

## API Definition

### Non-group-by queries

```json
{
"select": ["attributeName1", "attributeName2", ...],
"filters": [
  {"attribute": "attributeName", "operator": "IN/LT/GT/EQUAL", "operands": ["left-value", "right-value"]}, // operands list has 2 values only when operator is "IN", otherwise has only 1 value
  {...} // more filter conditions, and they are all conjunctive
]
}
```

### group-by queries

```json
{
"groupBy": ["attributeName1", "attributeName2", ...],
"aggFunction": "SUM/COUNT",
"aggAttribute": "attributeName",
"filters": [
  {"attribute": "attributeName", "operator": "IN/LT/GT/EQUAL", "operands": ["left-value", "right-value"]}, // operands list has 2 values only when operator is "IN", otherwise has only 1 value
  {...} // more filter conditions, and they are all conjunctive
]
}
```

## Config file

```yaml
serverConfig:
  hostname: localhost
  port: 8080

dbConfig:
  url: jdbc:postgresql://localhost/pinmap
  username: postgres
  password: pinmap

sampleConfig:
  baseTableName: tweets1 =
  baseTableColumnNames:
    - create_at
    - x # if base table uses [Point] datatype, here could use "coordinate[0]"
    - y # if base table uses [Point] datatype, here could use "coordinate[1]"
  baseTableSize: 1026036 # the number of records in base table
  sampleTableName: s_tweets1 # the table name for the sample, which will be generated when server starts
  sampleTableColumnNames: # list of columns' names in the sample table, *must be in the same order with base table
    - create_at
    - x
    - y
  sampleTableColumnTypes: # type of each column in sample table, *must be in the same order with column names
    - timestamp # the data types supported are: number, int, integer, bigint, timestamp, double, string
    - double
    - double

paramConfig:
  dimensions: # names of columns that can be group-by on or filter on
    - create_at
  epsilon: 0.05 # epsilon - error bound for approximate query result
```