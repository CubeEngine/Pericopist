package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.io.File;

public abstract class AbstractSourceConfig implements SourceConfig
{
    protected File directory;

    public File getDirectory()
    {
        return this.directory;
    }
}
