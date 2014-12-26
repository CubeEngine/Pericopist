# Usage

## Manual Usage

The class ```de.cubeisland.messageextractor.MessageCatalog``` is the main part of the library. This class has methods to generate a completely new message catalog or update the existing one. Also the updating process will generate a completely new message catalog if currently no catalog exists. To generate a new catalog use the ```generateCatalog()``` method and for updating the existing catalog the ```updateCatalog()``` method.

To create a new ```de.cubeisland.messageextractor.MessageCatalog``` class, the constructor needs a ```de.cubeisland.messageextractor.extractor.ExtractorConfiguration``` and a ```de.cubeisland.messageextractor.format.CatalogConfiguration```instance. Optionally a ```java.util.logging.Logger``` can be specified. Furthermore configurations have to be set up correctly, elsewise the validation of the configuration will fail, throwing an exception in ```de.cubeisland.messageextractor.MessageCatalog```. 

The ```de.cubeisland.messageextractor.extractor.ExtractorConfiguration``` needs to be configured accordingly to the projects programming language from which the translatable messages have to be extracted. Have a look at the [extractor directoy](extractor) of the documentation to see a list of all supported languages and to get a more detailed description. 

The ```de.cubeisland.messageextractor.extractor.ExtractorConfiguration``` needs to be configured accordingly to the personal opinion. Have a look at the [format directory](format) of the documentation to see a list of all available catalog formats and to get a more detailed description.

# Configuration

With the help of the ```de.cubeisland.messageextractor.MessageCatalogFactory``` class is it possible to set the configurations with a single xml file. That is a much easier way to get a ```de.cubeisland.messageextractor.MessageCatalog``` class. Simple call one of the ```getMessageCatalog(...)``` methods. At least a String to the xml resource and a ```java.nio.charset.Charset``` must be specified. A ```java.util.logging.Logger``` and a ```org.apache.velocity.context.Context``` instance are optional. The latter specifies a velocity context which is used to evaluate the configuration and to replace the [Velocity language](http://velocity.apache.org/engine/devel/user-guide.html) 

## Configuration example:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<extractor charset="utf-8" parent="path">
  <source language="LANGUAGE">
    <!-- ... -->
  </source>
  <catalog format="FORMAT">
    <!-- ... -->
  </catalog>
</extractor>
```

| Node | Type | Description | Attributes | Subtags |
|------|------|-------------|------------|---------|
| extractor | Tag | root node of the xml | charset, parent | source, catalog |
| charset | Attribute | encoding which shall be used to process the data<br/>default is the charset specified by the method invocation | none | none |
| parent | Attribute | path to a parent configuration which is extended by the current one | none | none |
| source | Tag | configuration for the extraction<br/>subtags are specified by the language attribute | language, charset | individual |
| language | Attribute | source language<br/>specifies the sub tags<br/>have a look at the [extractor directoy](extractor) | none | none |
| catalog | Tag | configuration for the catalog<br/>subtags are specified by the format attribute | format, charset | individual |
| format | Attribute | format of the catalg<br/>specifies the sub tags<br/>have a look at the [format directory](format) | none | none |

# Further Information

Have a look at the [test sources](https://github.com/CubeEngine/messageextractor/tree/master/core/src/test/java/de/cubeisland/messageextractor) of the project to get further information. 