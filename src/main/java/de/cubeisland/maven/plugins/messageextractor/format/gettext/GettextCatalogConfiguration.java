package de.cubeisland.maven.plugins.messageextractor.format.gettext;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.maven.plugins.messageextractor.format.AbstractCatalogConfiguration;
import de.cubeisland.maven.plugins.messageextractor.format.HeaderSection;

@XmlRootElement(name = "catalog")
public class GettextCatalogConfiguration extends AbstractCatalogConfiguration
{
    @XmlElement(name = "header")
    private HeaderSection headerSection;

    public HeaderSection getHeaderSection()
    {
        return this.headerSection;
    }
}
