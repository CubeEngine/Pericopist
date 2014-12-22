# Java

Configuration class: ```de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration```

## Settings:

- **charset:** is used for compiling the sources
- **directory:** directory storing the source files
- **classpath:** specifying paths to source dependencies
- **translatables:** a translatable describes how messages will be extracted from the source code
  - **method:** describes the extraction of a message from a method invocation
  - **constructor:** describes the extraction of a message from a constructor invocation
  - **annotation:** describes the extraction of a message from an annotation
  
Comments helping the translator translating the messages will be extracted from the source code too.
To do so write a comment starting with /// instead of // before or above the translatable expression.

for example:

```java
  /// I'll be an extracted comment
  this.translate("translated method");
  
  // or
  
  this.translate("translated method"); /// I'll be an extracted comment
```

### Constructor

Use the class ```de.cubeisland.messageextractor.extractor.java.configuration.Constructor``` to set up the extraction from a constructor invocation.

- **name:** fully qualified name
- **signature:** specifies the arguments of the constructor (arguments must be specified in the correct order!)
  - **type:** fully qualified name of the argument class
  - **usage:** usage of the argument. use one of NONE, SINGULAR, PLURAL and CONTEXT
    - **NONE:** the argument won't be used for the extraction process. It'll just be used for identifying the constructor (optional)
    - **SINGULAR:** the singular of the message will be extracted from this argument. (obligatory, can just be used once)
    - **PLURAL:** the plural of the message will be extracted from this argument. (optional, but can just be used once)
    - **CONTEXT:** the context of the message will be extracted from this argument (optional, but can just be used once). The context allows it to provide different translations for exactly the same message
- **description:** A description of this message (optional). This information will be written to the message catalog to help the translators translating the messages
- **defaultContext:** A default context of a message extracted with this method (optional)

### Method

Use the class ```de.cubeisland.messageextractor.extractor.java.configuration.Method``` to set up the extraction of a message from a method invocation.

- **static:** static or non static method
- all of the settings from a constructor

### Annotation

Use the class ```de.cubeisland.messageextractor.extractor.java.configuration.Annotation``` to set up the extraction of annotation fields.

- **name:** fully qualified name of the annotation
- **fields:** names of the fields containing singular messages
- **contextField:** field containing the context of the messages
- **targets:** specifying the targets from which the annotation fields will be extracted (default all targets).<br/>available targets: type, field, method, parameter, constructor, local_variable, annotation_type, package
- **description:** A description of this message (optional). This information will be written to the message catalog to help the translators translating the messages
- **defaultContext:** A default context of a message extracted with this method (optional)

## XML-Configuration

```xml
<!-- ... -->
<source language="java" charset="utf-8"> <!-- default charset: charset set as extractor tag attribute -->
  <directory>source file path</directory> <!-- default: ./src/main/java -->
  <classpath> <!-- default: System.getProperty("java.class.path").split(File.pathSeparator) -->
    <entry>entry</entry>
    <entry>another_entry</entry>
  </classpath>
  <translatables> <!-- register ways how to extract messages -->
    <method>
      <!-- ... -->
    </method>
    <constructor>
      <!-- ... -->
    </constructor>
    <annotation>
      <!-- ... -->
    </annotation>
  </translatables>
</source>
<!-- ... -->
```

### Method

```xml
<!-- ... -->
<method static="false"> <!-- default static: false -->
  <name>I.am.the.class#methodname</name>
  <signature>
    <type>int</type>
    <type use-as="singular">java.lang.String</type>
    <type use-as="plural">java.lang.String</type>
    <type>java.lang.Object[]</type>
  </signature>
  <description>I am a description of the method</description>
</method>
<!-- ... -->
```

### Constructor

```xml
<!-- ... -->
<constructor>
  <name>I.am.the.class</name>
  <signature>
    <type use-as="singular">java.lang.String</type>
    <type>java.lang.Object[]</type>
  </signature>
  <description>I am a description of the constructor</description>
</constructor>
<!-- ... -->
```

### Annotation

```xml
<!-- ... -->
<annotation>
  <name>I.am.the.annotation</name>
  <fields> <!-- default field: value -->
    <field>translatable_field</field>
    <field>second_translatable_field</field>
  </fields>
  <targets> <!-- defines target element types of the annotation -->
    <target>method</target>
    <target>type</target>
  </targets>
  <description>I am a description of the annotation</description>
</annotation>
<!-- ... -->
```
