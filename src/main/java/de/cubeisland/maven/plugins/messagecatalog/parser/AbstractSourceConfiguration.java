package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;

public abstract class AbstractSourceConfiguration implements SourceConfiguration
{
    @XmlElement
    protected File directory = new File("./src/main/java");

    public File getDirectory()
    {
        return this.directory;
    }
}
