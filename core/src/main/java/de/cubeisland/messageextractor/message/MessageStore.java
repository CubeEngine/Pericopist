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
package de.cubeisland.messageextractor.message;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class MessageStore implements Iterable<TranslatableMessage>
{
    private Set<TranslatableMessage> messages;

    public MessageStore()
    {
        this.messages = new TreeSet<TranslatableMessage>();
    }

    public void addMessage(String singular, String plural, Occurrence occurrence)
    {
        this.addMessage(singular, plural, null, occurrence);
    }

    public void addMessage(String singular, String plural, Integer position)
    {
        this.addMessage(singular, plural, position, null);
    }

    private void addMessage(String singular, String plural, Integer position, Occurrence occurrence)
    {
        TranslatableMessage message = this.getMessage(singular, plural);
        if (message != null)
        {
            message.addOccurrence(occurrence);
        }
        else
        {
            if (position != null)
            {
                message = new TranslatableMessage(singular, plural, position);
            }
            else
            {
                message = new TranslatableMessage(singular, plural, occurrence);
            }
            this.messages.add(message);
        }
    }

    public TranslatableMessage getMessage(String singular, String plural)
    {
        for (TranslatableMessage message : this.messages)
        {
            if (message.getSingular().equals(singular) && (plural == null && !message.hasPlural() || message
                .hasPlural() && message.getPlural().equals(plural)))
            {
                return message;
            }
        }
        return null;
    }

    public Set<TranslatableMessage> getMessages()
    {
        return messages;
    }

    public Iterator<TranslatableMessage> iterator()
    {
        return this.getMessages().iterator();
    }
}
