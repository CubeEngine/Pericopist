package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;

public abstract class AbstractCatalogConfiguration implements CatalogConfiguration
{
    @XmlElement(name = "template", required = true)
    protected File templateFile;

    @XmlElement
    protected boolean removeUnusedMessages = true;

    public final File getTemplateFile()
    {
        return templateFile;
    }

    public final boolean getRemoveUnusedMessages()
    {
        return removeUnusedMessages;
    }
}
