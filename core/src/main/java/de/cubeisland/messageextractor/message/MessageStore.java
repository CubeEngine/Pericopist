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
        this.messages = new TreeSet<>();
    }

    public void addMessage(TranslatableMessage message)
    {
        if (this.messages.contains(message))
        {
            throw new IllegalArgumentException("The specified message exists already and can't be added to the message store.");
        }

        this.messages.add(message);
    }

    public TranslatableMessage getMessage(String context, String singular)
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

            if (message.getSingular().equals(singular))
            {
                return message;
            }
        }
        return null;
    }

    public Set<TranslatableMessage> getMessages()
    {
        return this.messages;
    }

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
