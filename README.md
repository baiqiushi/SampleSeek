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
