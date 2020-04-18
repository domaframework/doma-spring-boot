# How to release

## Create release branch

Switch to a new branch to work on the release.

```
git switch -c release-x.y.z
```

## Update README

Update the version described in the README.

The following example shows the command to update the description from 1.3.0 to 1.4.0.

```
sed -i '' -e 's/1\.3\.0/1\.4\.0/g' README.md
```

## Set a new version

Update pom.xml by Maven Versions Plugin.

```
./mvnw versions:set -DnewVersion=x.y.z
```

Run all tests.

```
./mvnw test
```

Commit the new version.

```
./mvnw versions:commit
```

## Push changes to GitHub

Do `git commit` changes to the README and pom.xml.

```
git add .
git commit -m "Release x.y.z"
```

And do `git push` to GitHub.

```
git push origin release-x.y.z
```

## Do release work on GitHub

Create a new PR from release branch and merge it into the master branch.

The CI job running after the merge releases a new version artifact to the Maven Central Repository.

Check https://repo.maven.apache.org/maven2/org/seasar/doma/boot/doma-spring-boot/ .


## Update doma-spring-boot-demo

Update pom.xml of [doma-spring-boot-demo](https://github.com/backpaper0/doma-spring-boot-demo) to set a new version of doma-spring-boot.

Note: doma-spring-boot-demo is a personal repository for backpaper0.

## Prepare a next SNAPSHOT version

First, pull the master branch.

```
git switch master
git pull
```

Create a new branch.

```
git switch -c prepare-next-snapshot-version
```

Set a next SNAPSHOT version to pom.xml and commit.

```
./mvnw versions:set -DnewVersion=x.z.z-SNAPSHOT
./mvnw versions:commit
```

Do `git commit` and push to GitHub.

```
git add .
git commit -m "Prepare a next SNAPSHOT version"
git push origin master
```

Finally, create a new PR and merge it into the master branch.

