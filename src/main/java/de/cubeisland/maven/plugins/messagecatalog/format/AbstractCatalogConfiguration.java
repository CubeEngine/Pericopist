package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.File;

public abstract class AbstractCatalogConfiguration implements CatalogConfiguration
{
    protected File templateFile;
    protected boolean removeUnusedMessages = true;

    public File getTemplateFile()
    {
        return templateFile;
    }

    public boolean getRemoveUnusedMessages()
    {
        return removeUnusedMessages;
    }
}
