package de.cubeisland.maven.plugins.messageextractor.extractor.java.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "method")
public class TranslatableMethod
{
    @XmlElement(required = true)
    private String name;

    @XmlElement(name = "singular")
    private int singularIndex = 1;

    @XmlElement(name = "plural")
    private int pluralIndex = -1;

    public String getName()
    {
        return this.name;
    }

    public int getSingularIndex()
    {
        return this.singularIndex - 1;
    }

    public boolean hasPlural()
    {
        return this.pluralIndex != -1;
    }

    public int getPluralIndex()
    {
        return this.pluralIndex - 1;
    }

    @Override
    public String toString()
    {
        return this.getName() + ":" + this.singularIndex + (this.hasPlural() ? "," + this.pluralIndex : "");
    }
}
