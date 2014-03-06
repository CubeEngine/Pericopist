package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.maven.plugins.messagecatalog.format.AbstractCatalogConfiguration;

@XmlRootElement(name = "catalog")
public class GettextCatalogConfiguration extends AbstractCatalogConfiguration
{
    @XmlElement
    private String header;

    public String getHeader()
    {
        return header;
    }

    public String getFormat()
    {
        return "gettext";
    }
}
