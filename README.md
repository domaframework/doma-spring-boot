# doma-spring-boot

Spring Boot Support for Doma

## Properties file configuration

``` properties
doma.dialect= # Dialect of database used by Doma. (STANDARD, SQLITE, DB2, MSSQL, MYSQL, POSTGRES, ORACLE, H2, HSQL)
doma.sql-file-repository= # Type of SqlFileRepository. (GREEDY_CACHE, NO_CACHE)
doma.naming= # Type of Naming (NONE, LOWER_CASE, UPPER_CASE, SNAKE_LOWER_CASE, SNAKE_UPPER_CASE, LENIENT_SNAKE_LOWER_CASE, LENIENT_SNAKE_UPPER_CASE, DEFAULT)
doma.exception-translation-enabled= # Whether convert JdbcException into DataAccessException.
doma.max-rows=0 # Limit for the maximum number of rows. Ignored unless this value is greater than 0.
doma.query-timeout=0 # Number of seconds the driver will wait for a Statement object to execute. Ignored unless this value is greater than 0.
doma.fetch-size=0 # Hint to the number of rows that should be fetched. Ignored unless this value is greater than 0.
doma.batch-size=0 # Size in executing PreparedStatement#addBatch(). Regarded as 1 unless this value is greater than 1.
doma.data-source-name= # Datasource name.
doma.exception-sql-log-type= # Type of SQL log in the exception. (RAW, FORMATTED, NONE)
```
[![Build Status](https://travis-ci.org/domaframework/doma-spring-boot.svg?branch=master)](https://travis-ci.org/domaframework/doma-spring-boot)

## Issue Tracking

[GitHub Issues](https://github.com/domaframework/doma-spring-boot/issues)

## Maven dependency

``` xml
<dependency>
    <groupId>org.seasar.doma.boot</groupId>
    <artifactId>doma-spring-boot-starter</artifactId>
    <version>1.0.0.RC2</version>
</dependency>
```

Add the following repository to use snapshots.

``` xml
<repository>
    <id>sonatype-snapshots</id>
    <name>Sonatype Snapshots</name>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</repository>
```

## License

Licensed under the Apache License, Version 2.0.
