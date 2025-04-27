# doma-spring-boot

Spring Boot Support for [Doma](https://github.com/domaframework/doma)

[![Java CI with Maven](https://github.com/domaframework/doma-spring-boot/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/domaframework/doma-spring-boot/actions?query=workflow%3A%22Java+CI+with+Maven%22)

## Document

[GitHub Wiki](https://github.com/domaframework/doma-spring-boot/wiki)

## Issue Tracking

[GitHub Issues](https://github.com/domaframework/doma-spring-boot/issues)

## Maven dependency

``` xml
<dependency>
    <groupId>org.seasar.doma.boot</groupId>
    <artifactId>doma-spring-boot-starter</artifactId>
    <version>2.4.0</version>
</dependency>
<dependency>
    <groupId>org.seasar.doma</groupId>
    <artifactId>doma-processor</artifactId>
    <version>3.6.0</version>
    <optional>true</optional>
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
