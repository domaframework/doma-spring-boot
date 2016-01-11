# doma-spring-boot

Spring Boot Support for Doma

## Properties file configuration

``` properties
doma.dialect= # Dialect of database used by Doma. (STANDARD, SQLITE, DB2, MSSQL, MYSQL, POSTGRES, ORACLE, H2, HSQL)
doma.sql-file-repository= # Type of SqlFileRepository. (GREEDY_CACHE, NO_CACHE)
doma.naming= # Type of Naming. (DEFAULT, LENIENT_SNAKE_LOWER_CASE, LENIENT_SNAKE_UPPER_CASE, LOWER_CASE, NONE, SNAKE_LOWER_CASE, SNAKE_UPPER_CASE, UPPER_CASE)
doma.max-rows= # Max Rows
doma.fetch-size= # Fetch Size
doma.query-timeout= # Query Timeout
doma.batch-size= # Batch Size
doma.exception-translation-enabled= # Whether convert JdbcException into DataAccessException.
doma.entity-listener-provider-bean-factory= # Get EntityListener from BeanFactory.
```

## Issue Tracking

[GitHub Issues](https://github.com/domaframework/doma-spring-boot/issues)

## Maven dependency

``` xml
<dependency>
    <groupId>org.seasar.doma.boot</groupId>
    <artifactId>doma-spring-boot-starter</artifactId>
    <version>1.0.0-SNAPSHOT</version>
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