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

import java.util.Set;
import java.util.TreeSet;

/**
 * The TranslatableMessage class represents a translatable message.
 * They're stored within a {@link de.cubeisland.messageextractor.message.MessageStore} instance.
 * <p/>
 * The TranslatableMessage-instances will be created withing
 * {@link de.cubeisland.messageextractor.format.CatalogFormat#read(de.cubeisland.messageextractor.format.CatalogConfiguration, java.io.InputStream)}
 * and {@link de.cubeisland.messageextractor.extractor.MessageExtractor#extract(de.cubeisland.messageextractor.extractor.ExtractorConfiguration, MessageStore)}
 *
 * @see de.cubeisland.messageextractor.message.MessageStore
 */
public class TranslatableMessage implements Comparable<TranslatableMessage>
{
    private final String context;
    private final String singular;
    private final String plural;

    private final Set<SourceReference> sourceReferences;

    /**
     * The constructor creates a new translatable message
     *
     * @param context  context of the message, or null if the message doesn't have a special context
     * @param singular singular of the message
     * @param plural   plural of the message, or null if the message doesn't have a plural
     */
    public TranslatableMessage(String context, String singular, String plural)
    {
        this.context = context;
        this.singular = singular;
        this.plural = plural;

        this.sourceReferences = new TreeSet<>();
    }

    /**
     * This method returns whether this message has a special context.
     * A context is needed to provide different translations for the same method.
     *
     * @return whether this message has a special context
     */
    public boolean hasContext()
    {
        return this.context != null;
    }

    /**
     * This method returns the special context of the message.
     *
     * @return context of the message
     *
     * @see #hasContext()
     */
    public String getContext()
    {
        return this.context;
    }

    /**
     * This method returns the singular of the message.
     *
     * @return singular of the message
     */
    public String getSingular()
    {
        return this.singular;
    }

    /**
     * This method returns whether this message has a plural.
     *
     * @return whether this message has a plural
     */
    public boolean hasPlural()
    {
        return this.plural != null;
    }

    /**
     * This method returns the plural of the message.
     *
     * @return plural of the message
     */
    public String getPlural()
    {
        return this.plural;
    }

    /**
     * This method adds a new source reference to the message
     *
     * @param sourceReference SourceReference instance which describes how the message was extracted
     */
    public void addSourceReference(SourceReference sourceReference)
    {
        this.sourceReferences.add(sourceReference);
    }

    /**
     * This method returns a Set containing every SourceReference instances for this message.
     * A source reference describes how the message was extracted.
     *
     * @return Set containing every source reference of the message
     */
    public Set<SourceReference> getSourceReferences()
    {
        return this.sourceReferences;
    }

    /**
     * This method is used internal. It has to be overwritten if a subclass
     * overrides the {@link #compareTo(TranslatableMessage)} method.
     *
     * @return whether this class overrides the {@link #compareTo(TranslatableMessage)} method.
     */
    protected boolean overridesCompareToMethod()
    {
        return false;
    }

    @Override
    public int compareTo(TranslatableMessage o)
    {
        if (o.overridesCompareToMethod())
        {
            return -o.compareTo(this);
        }

        // compare context
        int comp = 0;
        if (this.hasContext())
        {
            if (o.hasContext())
            {
                comp = this.getContext().compareTo(o.getContext());
            }
            else
            {
                return 1;
            }
        }
        else if (o.hasContext())
        {
            return -1;
        }

        if (comp != 0)
        {
            return comp;
        }

        // compare singular
        comp = this.singular.compareTo(o.singular);
        if (comp != 0)
        {
            return comp;
        }

        // compare plural
        if (this.hasPlural())
        {
            if (o.hasPlural())
            {
                return this.plural.compareTo(o.plural);
            }
            return 1;
        }
        else if (o.hasPlural())
        {
            return -1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || this.getClass() != o.getClass())
        {
            return false;
        }

        TranslatableMessage that = (TranslatableMessage) o;

        // compare context
        if (!this.hasContext())
        {
            if (that.hasContext())
            {
                return false;
            }
        }
        else if (!this.getContext().equals(that.getContext()))
        {
            return false;
        }

        // compare singular
        if (!this.getSingular().equals(that.getSingular()))
        {
            return false;
        }

        // compare plural
        if (!this.hasPlural())
        {
            if (that.hasPlural())
            {
                return false;
            }
        }
        else if (!this.getPlural().equals(that.getPlural()))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = this.getSingular().hashCode();
        result = 31 * result + (this.hasContext() ? this.getContext().hashCode() : 0);
        result = 31 * result + (this.hasPlural() ? this.getPlural().hashCode() : 0);

        result = 31 * result + this.getSourceReferences().hashCode();

        return result;
    }
}
