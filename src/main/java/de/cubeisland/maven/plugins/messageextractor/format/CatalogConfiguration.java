package de.cubeisland.maven.plugins.messageextractor.format;

import java.io.File;

import de.cubeisland.maven.plugins.messageextractor.Configuration;

public interface CatalogConfiguration extends Configuration
{
    File getTemplateFile();

    String getCharsetName();
}
