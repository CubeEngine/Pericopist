package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.File;

public abstract class AbstractCatalogConfig implements CatalogConfig
{
    protected File templateFile;
    private boolean removeUnusedMessages;

    public File getTemplateFile()
    {
        return templateFile;
    }

    public boolean getRemoveUnusedMessages()
    {
        return removeUnusedMessages;
    }
}
