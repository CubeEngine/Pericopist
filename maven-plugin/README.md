messageextractor-maven-plugin
===========================

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
    <groupId>de.cubeisland.maven.plugins</groupId>
    <artifactId>messageextractor-maven-plugin</artifactId>
    <version>2.1.0</version>
    <configuration>
        <configurations> <!-- paths to configuration files; first existing configuration will be used -->
            <configuration>${basedir}/extractor.xml</configuration>
            <configuration>${basedir}/../extractor.xml</configuration>
        </configurations>
    </configuration>
</plugin>
```

Now use the command line, set the root directory of the project as the working dir and type

```mvn de.cubeisland.maven.plugins:messageextractor-maven-plugin:goalname```

so for generating a new message catalog ```mvn de.cubeisland.maven.plugins:messageextractor-maven-plugin:generate``` and for updating the existing one ```mvn de.cubeisland.maven.plugins:messageextractor-maven-plugin:update```.

**Please note:** Make sure the maven directory is included in the PATH environment variable.

To create a configuration file setting the messageextractor up read the [usage part](https://github.com/CubeEngine/messageextractor/blob/master/core/doc/usage.md) of the core module documentation.
