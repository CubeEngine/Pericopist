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

import org.fedorahosted.tennera.jgettext.Message;

import org.cubeengine.pericopist.message.TranslatableMessage;

/**
 * An instance of this class represents an {@link TranslatableMessage} extracted from an
 * existing gettext catalog. It stores every additional information which was stored within
 * the existing catalog. The extra information will be added to the new catalog also.
 */
abstract class GettextMessage extends TranslatableMessage
{
    private final int position;

    /**
     * The constructor creates a new instance of this class.
     *
     * @param context  message context
     * @param singular message singular
     * @param plural   message plural or null of it doesn#t exist
     * @param position position within the catalog. helps to sort the catalog entries
     *                 old entries are at the top of the catalog and new at the bottom.
     */
    protected GettextMessage(String context, String singular, String plural, int position)
    {
        super(context, singular, plural);

        this.position = position;
    }

    @Override
    protected boolean overridesCompareToMethod()
    {
        return true;
    }

    @Override
    public int compareTo(TranslatableMessage o)
    {
        if (o instanceof GettextMessage)
        {
            return Integer.compare(this.position, ((GettextMessage) o).position);
        }
        return -1;
    }

    /**
     * This method converts this gettext message into a {@link Message} object which is
     * used within the jgettext dependency.
     *
     * @return {@link Message}
     */
    public abstract Message toMessage();
}
