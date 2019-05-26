package edu.uci.ics.cloudberry.sampleseek.util;

import java.sql.Timestamp;

public class Config {

    public class DBConfig {
        private String url;
        private String username;
        private String password;

        public DBConfig() {
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUrl() {
            return url;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }

    public class SampleConfig {
        private String baseTableName;
        private String[] baseTableColumnNames;
        private int baseTableSize;
        private String sampleTableName;
        private String[] sampleTableColumnNames;
        private String[] sampleTableColumnTypes;
        private Class[] sampleTalbeColumnClasses;

        public SampleConfig() {
        }

        public void setBaseTableName(String baseTableName) {
            this.baseTableName = baseTableName;
        }

        public void setBaseTableColumnNames(String[] baseTableColumnNames) {
            this.baseTableColumnNames = baseTableColumnNames;
        }

        public void setBaseTableSize(int baseTableSize) {
            this.baseTableSize = baseTableSize;
        }

        public void setSampleTableName(String sampleTableName) {
            this.sampleTableName = sampleTableName;
        }

        public void setSampleTableColumnNames(String[] sampleTableColumnNames) {
            this.sampleTableColumnNames = sampleTableColumnNames;
        }

        public void setSampleTableColumnTypes(String[] sampleTableColumnTypes) {
            this.sampleTableColumnTypes = sampleTableColumnTypes;
            this.sampleTalbeColumnClasses = new Class[this.sampleTableColumnTypes.length];
            for (int i = 0; i < this.sampleTableColumnTypes.length; i ++) {
                Class colClass = null;
                switch (this.sampleTableColumnTypes[i].toLowerCase()) {
                    case "bigint":
                        colClass = Long.class;
                        break;
                    case "timestamp":
                        colClass = Timestamp.class;
                        break;
                    case "double":
                        colClass = Double.class;
                        break;
                    default:
                        colClass = String.class;
                        break;
                }
                this.sampleTalbeColumnClasses[i] = colClass;
            }
        }

        public void setSampleTalbeColumnClasses(Class[] sampleTalbeColumnClasses) {
            this.sampleTalbeColumnClasses = sampleTalbeColumnClasses;
        }

        public String getBaseTableName() {
            return baseTableName;
        }

        public String[] getBaseTableColumnNames() {
            return baseTableColumnNames;
        }

        public int getBaseTableSize() {
            return baseTableSize;
        }

        public String getSampleTableName() {
            return sampleTableName;
        }

        public String[] getSampleTableColumnNames() {
            return sampleTableColumnNames;
        }

        public String[] getSampleTableColumnTypes() {
            return sampleTableColumnTypes;
        }

        public Class[] getSampleTalbeColumnClasses() {
            return sampleTalbeColumnClasses;
        }
    }

    public class ParamConfig {
        private String[] dimensions;
        private double epsilon;

        public ParamConfig() {
        }

        public void setDimensions(String[] dimensions) {
            this.dimensions = dimensions;
        }

        public void setEpsilon(double epsilon) {
            this.epsilon = epsilon;
        }

        public String[] getDimensions() {
            return dimensions;
        }

        public double getEpsilon() {
            return epsilon;
        }
    }

    private DBConfig dbConfig;
    private SampleConfig sampleConfig;
    private ParamConfig paramConfig;

    public Config() {
    }

    public void setDbConfig(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public void setSampleConfig(SampleConfig sampleConfig) {
        this.sampleConfig = sampleConfig;
    }

    public void setParamConfig(ParamConfig paramConfig) {
        this.paramConfig = paramConfig;
    }

    public DBConfig getDbConfig() {
        return dbConfig;
    }

    public SampleConfig getSampleConfig() {
        return sampleConfig;
    }

    public ParamConfig getParamConfig() {
        return paramConfig;
    }
}
