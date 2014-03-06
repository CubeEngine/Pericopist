package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.File;

import de.cubeisland.maven.plugins.messagecatalog.Configuration;

public interface CatalogConfiguration extends Configuration
{
    File getTemplateFile();
}
