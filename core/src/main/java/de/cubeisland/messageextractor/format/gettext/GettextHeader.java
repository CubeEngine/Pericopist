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
package de.cubeisland.messageextractor.format.gettext;

import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.cubeisland.messageextractor.format.HeaderConfiguration;
import de.cubeisland.messageextractor.format.HeaderConfiguration.MetadataEntry;

class GettextHeader extends GettextMessage
{
    private final static String HEADER_ID = "HEADER_MESSAGE";

    private final List<MetadataEntry> entries;
    private final Collection<String> comments;

    public GettextHeader(Message header)
    {
        super(null, HEADER_ID, null, 0);

        this.comments = header.getComments();

        String[] entries = header.getMsgstr().split("\n");

        this.entries = new ArrayList<>(entries.length);
        for (String entry : entries)
        {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2)
            {
                throw new IllegalArgumentException(); // TODO modify exception
            }

            this.entries.add(this.createMetadataEntry(parts[0].trim(), parts[1].trim(), false));
        }
    }

    public GettextHeader(GettextCatalogConfiguration configuration)
    {
        super(null, HEADER_ID, null, 0);

        this.comments = new ArrayList<>();
        this.entries = new ArrayList<>(6);

        HeaderConfiguration headerConfiguration = configuration.getHeaderConfiguration();
        if (headerConfiguration == null)
        {
            return;
        }

        Collections.addAll(this.comments, headerConfiguration.getComments().split("\n"));
        Collections.addAll(this.entries, headerConfiguration.getMetadata());
    }

    public Collection<String> getComments()
    {
        return this.comments;
    }

    public MetadataEntry getEntry(int index)
    {
        return this.entries.get(index);
    }

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

    private MetadataEntry createMetadataEntry(String key, String value, boolean variable)
    {
        MetadataEntry metadataEntry = new MetadataEntry();

        metadataEntry.setKey(key);
        metadataEntry.setValue(value);
        metadataEntry.setVariable(variable);

        return metadataEntry;
    }
}
