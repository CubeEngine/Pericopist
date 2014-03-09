package de.cubeisland.maven.plugins.messageextractor.format.gettext;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.maven.plugins.messageextractor.format.AbstractCatalogConfiguration;

@XmlRootElement(name = "catalog")
public class GettextCatalogConfiguration extends AbstractCatalogConfiguration
{
    @XmlElement(name = "header")
    private GettextHeaderConfiguration headerConfiguration;

    public GettextHeaderConfiguration getHeaderConfiguration()
    {
        return this.headerConfiguration;
    }
}
