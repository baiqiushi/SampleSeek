package edu.uci.ics.cloudberry.sampleseek;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SampleManager {

    private final String separator = " | ";
    private String url = null;
    private String username = null;
    private String password = null;

    private String baseTableName = null;
    private String[] baseTableColumnNames = null;
    private String sampleTableName = null;
    private String[] sampleTableColumnNames = null;
    private String[] sampleTableColumnTypes = null;
    private Class[] sampleTableColumnClasses = null;

    private final int size = 10;


    private Connection conn = null;
    private Map<String, Object> sample = new HashMap<String, Object>();

    public SampleManager() {

        this.url = SampleSeekMain.config.getDbConfig().getUrl();
        this.username = SampleSeekMain.config.getDbConfig().getUsername();
        this.password = SampleSeekMain.config.getDbConfig().getPassword();

        this.baseTableName = SampleSeekMain.config.getSampleConfig().getBaseTableName();
        this.baseTableColumnNames = SampleSeekMain.config.getSampleConfig().getBaseTableColumnNames();
        this.sampleTableName = SampleSeekMain.config.getSampleConfig().getSampleTableName();
        this.sampleTableColumnNames = SampleSeekMain.config.getSampleConfig().getSampleTableColumnNames();
        this.sampleTableColumnTypes = SampleSeekMain.config.getSampleConfig().getSampleTableColumnTypes();
        this.sampleTableColumnClasses = SampleSeekMain.config.getSampleConfig().getSampleTalbeColumnClasses();

        // initialize the sample data structure
        for (int i = 0; i < this.sampleTableColumnNames.length; i ++) {
            String name = this.sampleTableColumnNames[i];
            this.sample.put(name, Array.newInstance(this.sampleTableColumnClasses[i], this.size));
        }
        // connect to database
        this.connect();
    }

    public boolean generateSample() {
        // Generate uniform sample with replacement from table
        String sql = "CREATE TABLE " + this.sampleTableName + " AS " +
                "WITH t AS (SELECT ";

        for (int j = 0; j < this.baseTableColumnNames.length; j ++) {
            if (j > 0) {
                sql += ", ";
            }
            sql += this.baseTableColumnNames[j] + " AS " + this.sampleTableColumnNames[j];
        }

        sql +=
        ", row_number() OVER () AS rn FROM " + this.baseTableName + ")\n" +
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
        String sql = "SELECT 1 FROM " + this.sampleTableName + " LIMIT 1";

        try {
            PreparedStatement statement = this.conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                exist = true;
            }
            statement.close();
        } catch (SQLException e) {
            //System.err.println(e.getMessage());
        }

        return exist;
    }

    public boolean loadSample() {
        String sql = "SELECT ";
        for (int j = 0; j < this.sampleTableColumnNames.length; j ++) {
            if (j > 0) {
                sql += ",";
            }
            sql += this.sampleTableColumnNames[j];
        }
        sql += " FROM " + this.sampleTableName;

        System.out.println("SQL: " + sql);

        try {
            PreparedStatement statement = this.conn.prepareStatement(sql);
            ResultSet rs = statement.executeQuery();
            int rsId = 0;
            while (rs.next()) {
                for (int j = 0; j < this.sampleTableColumnNames.length; j ++) {
                    String columnName = this.sampleTableColumnNames[j];
                    Object columnStore = this.sample.get(columnName);
                    Array.set(columnStore, rsId, this.sampleTableColumnClasses[j].cast(rs.getObject(columnName)));
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
            for (int j = 0; j < this.sampleTableColumnNames.length; j ++) {
                sb.append(separator);
                sb.append(Array.get(this.sample.get(this.sampleTableColumnNames[j]), i));
            }
            System.out.println(sb.toString());
        }
    }

    public void connect() {
        try {
            this.conn = DriverManager.getConnection(this.url, this.username, this.password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
