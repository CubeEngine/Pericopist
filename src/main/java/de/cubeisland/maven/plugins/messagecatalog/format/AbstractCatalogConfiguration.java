package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.File;

import javax.xml.bind.annotation.XmlElement;

public abstract class AbstractCatalogConfiguration implements CatalogConfiguration
{
    @XmlElement(name = "template", required = true)
    protected File templateFile;

    @XmlElement
    protected boolean removeUnusedMessages = true;

    @XmlElement
    protected boolean createEmptyTemplate = false;

    @XmlElement
    protected boolean deleteOldTemplate = false;

    public final File getTemplateFile()
    {
        return this.templateFile;
    }

    public final boolean getRemoveUnusedMessages()
    {
        return this.removeUnusedMessages;
    }

    public final boolean getCreateEmptyTemplate()
    {
        return this.createEmptyTemplate;
    }

    public final boolean getDeleteOldTemplate()
    {
        return this.deleteOldTemplate;
    }
}
