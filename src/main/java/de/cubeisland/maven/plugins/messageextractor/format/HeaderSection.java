package de.cubeisland.maven.plugins.messageextractor.format;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import de.cubeisland.maven.plugins.messageextractor.util.Misc;

@XmlRootElement(name = "header")
public class HeaderSection
{
    @XmlElement
    private String comments;

    @XmlElementWrapper(name = "metadata")
    @XmlElement(name = "entry")
    private List<MetadataEntry> metadata;

    public String getCommentsResource()
    {
        return this.comments;
    }

    public List<MetadataEntry> getMetadata()
    {
        return this.metadata;
    }

    public String getComments(Context velocityContext) throws IOException
    {
        URL commentsUrl = Misc.getResource(this.getCommentsResource());
        if (commentsUrl == null)
        {
            throw new FileNotFoundException("The header comments resource '" + this.getCommentsResource() + "' was not found in file system or as URL.");
        }

        VelocityEngine engine = new VelocityEngine();
        engine.init();

        StringWriter stringWriter = new StringWriter();
        engine.evaluate(velocityContext, stringWriter, "catalog_header_comments", Misc.getContent(commentsUrl));

        return stringWriter.toString();
    }

    public static class MetadataEntry
    {
        @XmlAttribute
        private String key;

        @XmlValue
        private String value;

        @XmlAttribute
        private boolean variable = false;

        public String getKey()
        {
            return this.key;
        }

        public String getValue()
        {
            return this.value;
        }

        public boolean isVariable()
        {
            return this.variable;
        }
    }
}
