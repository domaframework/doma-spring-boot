# How to release

## Switch to a master branch

Switch to a master branch:

```
git switch master
```

## Update README

Update the version described in the README.

The following example shows the command to update the description from 1.3.0 to 1.4.0:

```
sed -i '' -e 's/1\.3\.0/1\.4\.0/g' README.md
```

Commit the change:

```
git add .
git commit -m "Update version in README.md"
```

## Prepare for a release

Do `mvn release:prepare`:

```
./mvnw --batch-mode -DreleaseVersion=1.4.0 -DdevelopmentVersion=1.5.0-SNAPSHOT release:clean release:prepare
```

The above command creates some commits and pushes them to GitHub.

## Do release work on GitHub

The CI job builds new version artifacts and copies them to the Maven Central Repository.

Check https://repo.maven.apache.org/maven2/org/seasar/doma/boot/doma-spring-boot/ .

## Update doma-spring-boot-demo

Update pom.xml of [doma-spring-boot-demo](https://github.com/backpaper0/doma-spring-boot-demo) to set a new version of doma-spring-boot.

Note: doma-spring-boot-demo is a personal repository for backpaper0.
