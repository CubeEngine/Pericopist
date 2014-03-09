package de.cubeisland.maven.plugins.messageextractor.format.gettext;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "header")
public class GettextHeaderConfiguration
{
    @XmlElement
    public String comments;

    @XmlElementWrapper(name = "metadata")
    @XmlElement(name = "entry")
    public List<MetadataEntry> metadata;

    private static class MetadataEntry
    {
        @XmlAttribute
        public String key;

        @XmlValue
        public String value;
    }
}
