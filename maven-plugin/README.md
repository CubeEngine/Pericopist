pericopist-maven-plugin
=============================

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.cubeengine.maven.plugins/pericopist-maven-plugin/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/org.cubeengine.maven.plugins/pericopist-maven-plugin)
(replace ${pericopistVersion} with this version)

# Description

This maven plugin generates message catalogs by parsing the source tree of the project and collecting all the
string literals into a message catalog

# Goals

- **generate:** generates a completely new message catalog
- **update:** updates the existing message catalog or generates a new one if it doesn't exist

# Usage

To use the plugin add it to the maven configuration of your project.

```xml
<plugin>
    <groupId>org.cubeengine.maven.plugins</groupId>
    <artifactId>pericopist-maven-plugin</artifactId>
    <version>${pericopistVersion}</version>
    <configuration>
        <configurations> <!-- paths to configuration files; first existing configuration will be used -->
            <configuration>${basedir}/extractor.xml</configuration>
            <configuration>${basedir}/../extractor.xml</configuration>
        </configurations>
        <readTimeout>10000</readTimeout> <!-- optional; adds a read timeout of 10 seconds; this is used for reading the config file -->
    </configuration>
</plugin>
```

Now use the command line, set the root directory of the project as the working dir and type

```mvn org.cubeengine.maven.plugins:pericopist-maven-plugin:${goal_name}```

so for generating a new message catalog ```mvn org.cubeengine.maven.plugins:pericopist-maven-plugin:generate``` and for updating the existing one ```mvn org.cubeengine.maven.plugins:pericopist-maven-plugin:update```.

**Please note:** Make sure the maven directory is included in the PATH environment variable.

To create a configuration file setting the pericopist up read the [usage part](https://github.com/CubeEngine/Pericopist/blob/master/core/doc/usage.md) of the core module documentation.
