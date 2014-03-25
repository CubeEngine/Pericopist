/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Phillip Schichtel, Stefan Wolf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.cubeisland.messageextractor.format;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import de.cubeisland.messageextractor.util.Misc;

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

    public String getComments(Charset charset, Context velocityContext) throws IOException
    {
        URL commentsUrl = Misc.getResource(this.getCommentsResource());
        if (commentsUrl == null)
        {
            throw new FileNotFoundException("The header comments resource '" + this.getCommentsResource() + "' was not found in file system or as URL.");
        }

        VelocityEngine engine = new VelocityEngine();
        engine.init();

        StringWriter stringWriter = new StringWriter();
        engine.evaluate(velocityContext, stringWriter, "catalog_header_comments", Misc.getContent(commentsUrl, charset));

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
