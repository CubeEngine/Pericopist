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

import org.fedorahosted.tennera.jgettext.Message;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class GettextHeader extends GettextMessage
{
    private final static String HEADER_ID = "HEADER_MESSAGE";

    private final Map<String, String> entries;
    private final Collection<String> comments;

    public GettextHeader(Message header)
    {
        super(null, HEADER_ID, null, 0);

        this.comments = header.getComments();

        String[] entries = header.getMsgstr().split("\n");

        this.entries = new HashMap<>(entries.length);
        for (String entry : entries)
        {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2)
            {
                throw new IllegalArgumentException(); // TODO modify exception
            }

            this.entries.put(parts[0], parts[1]);
        }
    }

    public Collection<String> getComments()
    {
        return this.comments;
    }

    public boolean hasEntry(String name)
    {
        return this.entries.containsKey(name);
    }

    public String getValue(String entry)
    {
        return this.entries.get(entry);
    }

    public int getEntrySize()
    {
        return this.entries.size();
    }
}
