# doma-spring-boot

Spring Boot Support for [Doma](https://github.com/domaframework/doma)

[![Build Status](https://travis-ci.org/domaframework/doma-spring-boot.svg?branch=master)](https://travis-ci.org/domaframework/doma-spring-boot)

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

## Issue Tracking

[GitHub Issues](https://github.com/domaframework/doma-spring-boot/issues)

## Maven dependency

``` xml
<dependency>
    <groupId>org.seasar.doma.boot</groupId>
    <artifactId>doma-spring-boot-starter</artifactId>
    <version>1.0.0</version>
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

## Tutorial

### Create a project

Create a blank project from [SPRING INITIALIZR](https://start.spring.io). 
Enter and select "Web", "JDBC" and "H2" in「Search for dependencies」.

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/87eb3d20-dda4-a52d-e1e7-9adb264aef44.png">

Then click 「Generate Project」 and `demo.zip` will be downloaded. Extract the zip and import the Maven project into IDE.
In this tutorial we will use IntelliJ IDEA. In case of IDEA, only you have to do is just open `pom.xml`.

Add the following dependency to `pom.xml` so that we can use Doma with Spring Boot.

``` xml
<dependency>
    <groupId>org.seasar.doma.boot</groupId>
    <artifactId>doma-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Create an entity

Next, create the following entity

``` java
package com.example;

import org.seasar.doma.Entity;
import org.seasar.doma.GeneratedValue;
import org.seasar.doma.GenerationType;
import org.seasar.doma.Id;

@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;
    public String name;
}
```

### Create a DAO interface

Then, create the following DAO interface. We will add search and insert method.

``` java
package com.example;

import org.seasar.doma.Dao;
import org.seasar.doma.Insert;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@ConfigAutowireable
@Dao
public interface ReservationDao {
    @Select
    List<Reservation> selectAll();

    @Insert
    @Transactional
    int insert(Reservation reservation);
}
```
`@ConfigAutowireable` is the annotation to annotate `@Repository` and `@Autowired` on generated DAO implematations using [@AnnotateWith](http://doma.readthedocs.org/ja/stable/config/#id22).

### Generate the DAO imlementation class

After create a DAO interface, build by IDE or Maven, then the implementation class will be generated. However, update methods need the corresponding SQL files **at the compile time**. Unless SQL files exist, compilation will fail.
Usually, SQL corresponding to the method will be written in `src/main/resources/META-INF/(FQCN)/(class name)/(method name).sql`. In this case, it's `src/main/resources/META-INF/com/example/ReservationDao/selectAll.sql`.

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/bc75a9c8-fdb4-aafc-e5a5-0921575b556d.png" />

**Tip**: On the image above, `selectAll` is highlighted by red because the required SQL file does not exist. This feature is enabled by [Doma Support Plugin](https://github.com/siosio/DomaSupport). This is not compulsory but really useful.

<img width="60%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/1aa25b5d-50e2-f19d-f539-92f6d6adc25b.png">

With this plugin, SQL file can be created easily. `Option + Enter` on the method, and select 「SQLファイルを作る。」 on the menu.

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/d008815c-f5b8-a4cf-4b85-2e7503bac42b.png" />

Then select `.../src/main/resouces` and SQL file will be generated.

<img width="40%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/f1486c0f-ba90-2f3a-d70a-dc962d1e0a76.png">


After that, write your query in this SQL file.

``` sql
SELECT
  id,
  name
FROM reservation
ORDER BY name ASC
```

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/dd62c85b-5ccb-13ef-6927-8e17f12903db.png" />

Build again, then compilation will succeed and you can see `ReservationDaoImpl` is generated under `target` and compiled.

<img src="https://qiita-image-store.s3.amazonaws.com/0/1852/2a23f92d-348a-d7ff-957a-027a80473983.png" />

### Create an application

Let's make a small application using `ReservationDao` in `DemoApplication`.

``` java
package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    ReservationDao reservationDao;

    // Insert data at initailizing phase using ReservationDao#insert
    @Bean
    CommandLineRunner runner() {
        return args -> Arrays.asList("spring", "spring boot", "spring cloud", "doma").forEach(s -> {
            Reservation r = new Reservation();
            r.name = s;
            reservationDao.insert(r);
        });
    }

    @RequestMapping(path = "/")
    List<Reservation> all() {
        return reservationDao.selectAll();
    }
}
```

Next configure the SQL dialect for Doma. In this case, we use H2 database, so set 

``` properties
doma.dialect=h2
```

in `application.properties`.

Property values can be suggested by `Ctrl + Space`.

<img width="60%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/b6416edb-748c-54e4-a473-0fcce5870963.png" />


<img width="60%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/92179170-839a-d3be-d05b-18f8e4fa0f3c.png" />

Doma does not generate schema, so DDL script is needed. Create `src/main/resources/schema.sql` as follows:

``` sql
CREATE TABLE reservation (
  id   IDENTITY,
  NAME VARCHAR(50)
);
```

**Tip**:
Executing `schema.sql` can be skipped by setting `spring.datasource.initialize=false`. This will be helpful in deploying.

Finally, run `main` method on `DemoApplication`, then the application will launch.

Access [http://localhost:8080](http://localhost:8080), the result of `selectAll.sql` will show.

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/c962a9a6-8eae-fc2f-167f-86af07988e8a.png" />


### Add a method

Add `selectByName` to try Doma's 2 way SQL.

``` java
@ConfigAutowireable
@Dao
public interface ReservationDao {
    @Select
    List<Reservation> selectAll();

    @Select
    List<Reservation> selectByName(String name);

    @Insert
    @Transactional
    int insert(Reservation reservation);
}
```

Write the correspoinding SQL in `src/main/resources/META-INF/com/example/ReservationDao/selectByName.sql`.

``` sql
SELECT
  id,
  name
FROM reservation
WHERE name LIKE /* @prefix(name) */'spring%' ESCAPE '$'
```

Check the [doc](http://doma.readthedocs.org/ja/stable/expression/#id11) for the special expression in SQL comment.

This SQL can be executed as it is!

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/2ec92660-9370-572e-8b54-eb169cf705f3.png" />

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/07c3a4ea-0786-95a7-b812-2630ccd4889b.png" />

Add the following method to Controller and call `ReservationDao#selectByName`.

``` java
    @RequestMapping(path = "/", params = "name")
    List<Reservation> name(@RequestParam String name) {
        return reservationDao.selectByName(name);
    }
```

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/618907ba-bdc0-ad8b-e33e-c3ae2c6f64d6.png" />

<img width="80%" src="https://qiita-image-store.s3.amazonaws.com/0/1852/d8f0ff19-f88b-dcee-e478-9a7ae7bae549.png" />

[Source code](https://github.com/making/doma2-spring-boot-demo)


## License

Licensed under the Apache License, Version 2.0.
