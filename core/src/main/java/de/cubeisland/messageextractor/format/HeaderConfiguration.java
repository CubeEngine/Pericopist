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

import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.cubeisland.messageextractor.configuration.Configuration;
import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.util.XmlCharsetAdapter;

@XmlRootElement(name = "header")
public class HeaderConfiguration
{
    private Charset charset;

    private String comments;
    private MetadataEntry[] metadata;

    public String getComments()
    {
        return comments;
    }

    @XmlElement(name = "comments")
    public void setComments(String comments)
    {
        this.comments = comments;
    }

    public MetadataEntry[] getMetadata()
    {
        return metadata;
    }

    @XmlElementWrapper(name = "metadata")
    @XmlElement(name = "entry")
    public void setMetadata(MetadataEntry... metadata)
    {
        this.metadata = metadata;
    }

    public static class MetadataEntry
    {
        private String key;
        private String value;
        private boolean variable = false;

        @XmlAttribute(name = "key")
        public void setKey(String key)
        {
            this.key = key;
        }

        public String getKey()
        {
            return this.key;
        }

        @XmlValue
        public void setValue(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return this.value;
        }

        @XmlAttribute
        public void setVariable(boolean variable)
        {
            this.variable = variable;
        }

        public boolean isVariable()
        {
            return this.variable;
        }
    }
}
