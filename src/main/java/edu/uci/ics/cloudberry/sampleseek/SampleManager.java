package edu.uci.ics.cloudberry.sampleseek;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SampleManager {

    private final String separator = " | ";

    private final String url = "jdbc:postgresql://localhost/pinmap";
    private final String user = "postgres";
    private final String password = "pinmap";
    private final int size = 10;

    private final String[] sampleColumnNames = {"id", "create_at", "x", "y"};
    private final Class[] sampleColumnTypes = {Long.class, Timestamp.class, Double.class, Double.class};


    private Connection conn = null;
    private Map<String, Object> sample = new HashMap<String, Object>();

    public SampleManager() {
        // initialize the sample data structure
        for (int i = 0; i < this.sampleColumnTypes.length; i ++) {
            String name = this.sampleColumnNames[i];
            this.sample.put(name, Array.newInstance(this.sampleColumnTypes[i], this.size));
        }
        // connect to database
        this.connect();
    }

    public boolean generateSample() {
        // Generate uniform sample with replacement from table
        String sql = "CREATE TABLE " + SampleSeekMain.sampleName + " AS " +
                "WITH t AS (SELECT *, row_number() OVER () AS rn FROM " + SampleSeekMain.tableName + ")\n" +
                "SELECT * FROM (\n" +
                "    SELECT trunc(random() * (SELECT max(rn) FROM t))::int + 1 AS rn\n" +
                "    FROM   generate_series(1, ?) g\n" +
                "    ) r\n" +
                "JOIN   t USING (rn)";

        try {
            PreparedStatement statement = this.conn.prepareStatement(sql);
            statement.setInt(1, this.size);
            statement.executeUpdate();
            statement.close();

            return true;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public boolean isSampeExist() {
        boolean exist = false;
        // Generate uniform sample with replacement from table
        String sql = "SELECT 1 FROM " + SampleSeekMain.sampleName + " LIMIT 1";

        try {
            PreparedStatement statement = this.conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                exist = true;
            }
            statement.close();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return exist;
    }

    public boolean loadSample() {
        String sql = "SELECT ";
        for (int j = 0; j < this.sampleColumnNames.length; j ++) {
            if (j > 0) {
                sql += ",";
            }
            sql += this.sampleColumnNames[j];
        }
        sql += " FROM " + SampleSeekMain.sampleName;

        System.out.println("SQL: " + sql);

        try {
            PreparedStatement statement = this.conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            int rsId = 0;
            while (rs.next()) {
                for (int j = 0; j < this.sampleColumnNames.length; j ++) {
                    String columnName = this.sampleColumnNames[j];
                    Object columnStore = this.sample.get(columnName);
                    Array.set(columnStore, rsId, this.sampleColumnTypes[j].cast(rs.getObject(columnName)));
                }
                rsId ++;
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return true;
    }

    public void printSample() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size; i ++) {
            sb.setLength(0);
            sb.append(i);
            for (int j = 0; j < this.sampleColumnNames.length; j ++) {
                sb.append(separator);
                sb.append(Array.get(this.sample.get(this.sampleColumnNames[j]), i));
            }
            System.out.println(sb.toString());
        }
    }

    public void connect() {
        try {
            this.conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
