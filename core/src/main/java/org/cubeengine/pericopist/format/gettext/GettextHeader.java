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
package org.cubeengine.pericopist.format.gettext;

import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.cubeengine.pericopist.exception.CatalogFormatException;
import org.cubeengine.pericopist.format.HeaderConfiguration;
import org.cubeengine.pericopist.format.HeaderConfiguration.MetadataEntry;

/**
 * This gettext message describes a gettext header.
 * It stores the extracted header entries and the comments.
 */
class GettextHeader extends GettextMessage
{
    private static final String HEADER_ID = "HEADER_MESSAGE";

    private final List<MetadataEntry> entries;
    private final Collection<String> comments;

    /**
     * This method creates a header message instance from an old gettext catalog.
     *
     * @param header jgettext {@link Message} instance which was parsed from the catalog
     *
     * @throws CatalogFormatException is thrown if a header entry has a wrong formatting and couldn't be read.
     */
    public GettextHeader(Message header) throws CatalogFormatException
    {
        super(null, HEADER_ID, null, 0);

        this.comments = header.getComments();

        String[] headerEntries = header.getMsgstr().split("\n");

        this.entries = new ArrayList<>(headerEntries.length);
        for (String entry : headerEntries)
        {
            final int partSize = 2;
            String[] parts = entry.split(":", partSize);
            if (parts.length != partSize)
            {
                throw new CatalogFormatException("The formatting of the header entry '" + entry + "' is wrong. It has to be 'key: value'.");
            }

            // every metadata entry isn't variable!!!
            this.entries.add(this.createMetadataEntry(parts[0].trim(), parts[1].trim(), false));
        }
    }

    /**
     * This method creates a header message instance from the current configuration.
     *
     * @param configuration gettext configuration
     */
    public GettextHeader(GettextCatalogConfiguration configuration)
    {
        super(null, HEADER_ID, null, 0);

        this.comments = new ArrayList<>();
        this.entries = new ArrayList<>();

        HeaderConfiguration headerConfiguration = configuration.getHeaderConfiguration();
        if (headerConfiguration == null)
        {
            return;
        }

        Collections.addAll(this.comments, headerConfiguration.getComments().split("\n"));
        Collections.addAll(this.entries, headerConfiguration.getMetadata());
    }

    /**
     * This method returns the comments from the header
     *
     * @return comments
     */
    public Collection<String> getComments()
    {
        return this.comments;
    }

    /**
     * This method returns the {@link MetadataEntry} instance which has the specified index.
     *
     * @param index index of the entry
     *
     * @return {@link MetadataEntry} instance
     */
    public MetadataEntry getEntry(int index)
    {
        return this.entries.get(index);
    }

    /**
     * This method returns the quantity of header entries
     *
     * @return quantity of header entries
     */
    public int getEntrySize()
    {
        return this.entries.size();
    }

    @Override
    public Message toMessage()
    {
        HeaderFields headerFields = new HeaderFields();
        for (MetadataEntry entry : this.entries)
        {
            headerFields.setValue(entry.getKey(), entry.getValue());
        }

        Message headerMessage = headerFields.unwrap();
        for (String comment : this.getComments())
        {
            headerMessage.addComment(comment);
        }

        return headerMessage;
    }

    /**
     * helper method which creates a {@link MetadataEntry}
     *
     * @param key      key of the entry
     * @param value    value of the entry
     * @param variable is this entry variable or static
     *
     * @return the new {@link MetadataEntry}
     */
    private MetadataEntry createMetadataEntry(String key, String value, boolean variable)
    {
        MetadataEntry metadataEntry = new MetadataEntry();

        metadataEntry.setKey(key);
        metadataEntry.setValue(value);
        metadataEntry.setVariable(variable);

        return metadataEntry;
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
        if (!super.equals(o))
        {
            return false;
        }

        final GettextHeader that = (GettextHeader)o;

        if (!comments.equals(that.comments))
        {
            return false;
        }
        if (!entries.equals(that.entries))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = super.hashCode();
        result = 31 * result + entries.hashCode();
        result = 31 * result + comments.hashCode();
        return result;
    }
}
