pericopist-core
=====================

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/pericopist-core/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.cubeengine/pericopist-core)
(replace ${pericopistVersion} with this version)

# Description

The core module of the pericopist provides the main functionality. It contains the code which
extracts the messages from the source code and generates a message catalog with that information.

Every other module uses the core and adds new functionality.

# Using the library

## Usage

You can find the information how to use the library in the [usage file](https://github.com/CubeEngine/Pericopist/blob/master/core/doc/usage.md) of the documentation.

## Dependencies

### Maven

To add the dependency using Maven just add the following section to your dependencies:
```xml
<dependency>
    <groupId>org.cubeengine</groupId>
    <artifactId>pericopist-core</artifactId>
    <version>${pericopistVersion}</version>
</dependency>
```

### Gradle

To add the dependency using Gradle just add the following line to your dependencies:
```groovy
compile 'org.cubeengine.pericopist-core:${pericopistVersion}'
```
