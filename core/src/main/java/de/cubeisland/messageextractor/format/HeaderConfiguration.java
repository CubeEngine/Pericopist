/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import de.cubeisland.messageextractor.configuration.Mergeable;
import de.cubeisland.messageextractor.configuration.MergeableArray;
import de.cubeisland.messageextractor.configuration.MergeableArrayMode;

/**
 * This class can be used for message catalogs which have header fields.
 */
@Mergeable(true)
@XmlRootElement(name = "header")
public class HeaderConfiguration
{
    private String comments;

    @MergeableArray(MergeableArrayMode.REPLACE_EXISTING_VALUES)
    private MetadataEntry[] metadata;

    /**
     * This method returns the comments which shall be added to the header
     *
     * @return comments which shall be added to the header
     */
    public String getComments()
    {
        return comments;
    }

    /**
     * This method sets the comments which shall be added to the header
     *
     * @param comments which shall be added to the header
     */
    @XmlElement(name = "comments")
    public void setComments(String comments)
    {
        this.comments = comments;
    }

    /**
     * This method returns every {@link de.cubeisland.messageextractor.format.HeaderConfiguration.MetadataEntry} which shall be added to the header.
     *
     * @return Metadata entries
     */
    public MetadataEntry[] getMetadata()
    {
        return metadata;
    }

    /**
     * This method sets the {@link de.cubeisland.messageextractor.format.HeaderConfiguration.MetadataEntry} instances which shall be added to the header
     *
     * @param metadata Metadata entries
     */
    @XmlElementWrapper(name = "metadata")
    @XmlElement(name = "entry")
    public void setMetadata(MetadataEntry... metadata)
    {
        this.metadata = metadata;
    }

    /**
     * This class represents metadata for a header which contains out of a key and a value.
     */
    public static class MetadataEntry
    {
        private String key;
        private String value;
        private boolean variable = false;

        /**
         * This method sets the key of the metadata entry.
         *
         * @param key key
         */
        @XmlAttribute(name = "key")
        public void setKey(String key)
        {
            this.key = key;
        }

        /**
         * This method returns the key of the metadata entry.
         *
         * @return key
         */
        public String getKey()
        {
            return this.key;
        }

        /**
         * This method sets the value of the metadata entry.
         *
         * @param value value
         */
        @XmlValue
        public void setValue(String value)
        {
            this.value = value;
        }

        /**
         * This method returns the value of the metadata entry.
         *
         * @return value
         */
        public String getValue()
        {
            return this.value;
        }

        /**
         * This method sets the metadata entry variable. That means that two
         * metadata entries which have the same key aren't different.
         * It can be used for fields like 'creation time'.
         *
         * @param variable whether this field is variable
         */
        @XmlAttribute
        public void setVariable(boolean variable)
        {
            this.variable = variable;
        }

        /**
         * This method returns whether this field is variable
         *
         * @return whether this field is variable
         *
         * @see #setVariable(boolean)
         */
        public boolean isVariable()
        {
            return this.variable;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            MetadataEntry that = (MetadataEntry) o;

            if (key != null ? !key.equals(that.key) : that.key != null)
            {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode()
        {
            return key != null ? key.hashCode() : 0;
        }
    }
}
