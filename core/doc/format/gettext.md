# Gettext

Configuration class: ```de.cubeisland.messageextractor.format.gettext.GettextCatalogConfiguration```

## Settings:

- **charset:** is used for reading and writing the catalogs
- **template:** message catalog file
- **removeUnusedMessages:** whether messages which are not used anymore will be removed (default true)
- **createEmptyTemplate:** whether the template will be created if it's empty (default false)
- **pluralAmount:** amount of plural messages of the source language (default 2)
- **header:** setting of the catalog header
  - **comments:** comments which will be appended to the header
  - **metadata:** metadata of the header like source language name or project id

## XML-Configuration

```xml
<!-- ... -->
<catalog format="gettext" charset="utf-8"> <!-- default charset: charset set as extractor tag attribute -->
  <removeUnusedMessages>true</removeUnusedMessages>
  <createEmptyTemplate>false</createEmptyTemplate>
  <pluralAmount>2</pluralAmount>
  <template>TEMPLATE PATH</template>
  <header>
    <comments>
      header comments of the template file.
      use ${resource.load("path")} to load an external file as comments
    </comments>
    <metadata>
      <entry key="Project-Id-Version">PACKAGE VERSION</entry>
      <entry key="POT-Creation-Date" variable="true">${date.get('yyyy-MM-dd HH:mm:ssZ')}</entry>
      <entry key="Last-Translator">FULL NAME &lt;EMAIL@ADDRESS&gt;</entry>
        ...
    </metadata>
  </header>
</catalog>
<!-- ... -->
```