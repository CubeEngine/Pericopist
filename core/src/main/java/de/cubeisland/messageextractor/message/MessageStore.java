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
package de.cubeisland.messageextractor.message;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * The MessageStore class is a helper class which is used to store {@link de.cubeisland.messageextractor.message.TranslatableMessage} instances.
 * The {@link de.cubeisland.messageextractor.format.CatalogFormat} and the {@link de.cubeisland.messageextractor.extractor.MessageExtractor}
 * use this class to communicate among each other.
 *
 * @see de.cubeisland.messageextractor.format.CatalogFormat
 * @see de.cubeisland.messageextractor.extractor.MessageExtractor
 * @see de.cubeisland.messageextractor.message.TranslatableMessage
 */
public class MessageStore implements Iterable<TranslatableMessage>
{
    private Set<TranslatableMessage> messages;

    /**
     * The constructor creates a new message store
     */
    public MessageStore()
    {
        this.messages = new TreeSet<>();
    }

    /**
     * This method adds a {@link de.cubeisland.messageextractor.message.TranslatableMessage} to the message store
     *
     * @param message message which shall be add
     *
     * @throws java.lang.IllegalArgumentException if the message exists already.
     */
    public void addMessage(TranslatableMessage message)
    {
        if (this.messages.contains(message))
        {
            throw new IllegalArgumentException("The specified message exists already and can't be added to the message store.");
        }

        this.messages.add(message);
    }

    /**
     * This method returns the {@link de.cubeisland.messageextractor.message.TranslatableMessage} with the specified data.
     *
     * @param context  context of the message
     * @param singular singular of the message
     * @param plural   plural of the message
     *
     * @return the {@link de.cubeisland.messageextractor.message.TranslatableMessage} which has the specified data or null
     */
    public TranslatableMessage getMessage(String context, String singular, String plural)
    {
        for (TranslatableMessage message : this.messages)
        {
            if (message.hasContext() && context == null)
            {
                continue;
            }
            else if (!message.hasContext() && context != null)
            {
                continue;
            }
            else if (message.hasContext() && !message.getContext().equals(context))
            {
                continue;
            }

            if (!message.getSingular().equals(singular))
            {
                continue;
            }

            if (message.hasPlural())
            {
                if (message.getPlural().equals(plural))
                {
                    return message;
                }
            }
            else if (plural == null)
            {
                return message;
            }
        }
        return null;
    }

    /**
     * This method returns a Set containing every {@link de.cubeisland.messageextractor.message.TranslatableMessage} instance
     *
     * @return Set containing every message
     */
    public Set<TranslatableMessage> getMessages()
    {
        return this.messages;
    }

    /**
     * This method returns the amount of message which are stored by this instance
     *
     * @return amount of messages
     */
    public int size()
    {
        return this.getMessages().size();
    }

    @Override
    public Iterator<TranslatableMessage> iterator()
    {
        return this.getMessages().iterator();
    }
}
