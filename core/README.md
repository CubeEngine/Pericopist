messageextractor-core
=====================

# Description

The core module of the message extractor provides the main functionality. It contains the code which 
extracts the messages from the source code and generates a message catalog with that information.

Every other module uses the core and adds new functionality.

# Using the library

## Usage

You can find the information how to use the library in the [usage file](doc/usage.md)
of the documentation.

## Dependencies

### Maven

To add the dependency using Maven just add the following section to your dependencies:
```xml
<dependency>
    <groupId>de.cubeisland</groupId>
    <artifactId>messageextractor-core</artifactId>
    <version>2.1.0</version>
</dependency>
```

### Gradle

To add the dependency using Gradle just add the following line to your dependencies:
```groovy
compile 'de.cubeisland:messageextractor-core:2.1.0'
```
