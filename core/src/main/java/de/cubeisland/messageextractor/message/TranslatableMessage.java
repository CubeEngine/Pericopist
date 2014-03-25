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

import java.util.Set;
import java.util.TreeSet;

public class TranslatableMessage implements Comparable<TranslatableMessage>
{
    private final String singular;
    private final String plural;
    private final Set<Occurrence> occurrences;

    private final Integer position;

    public TranslatableMessage(String singular, String plural, Integer position)
    {
        this(singular, plural, position, null);
    }

    public TranslatableMessage(String singular, String plural, Occurrence firstOccurrence)
    {
        this(singular, plural, null, firstOccurrence);
    }

    private TranslatableMessage(String singular, String plural, Integer position, Occurrence firstOccurrence)
    {
        this.position = position;

        this.singular = singular;
        this.plural = plural;

        this.occurrences = new TreeSet<Occurrence>();
        if (firstOccurrence != null)
        {
            this.occurrences.add(firstOccurrence);
        }
    }

    public void addOccurrence(Occurrence occurrence)
    {
        this.occurrences.add(occurrence);
    }

    public String getSingular()
    {
        return this.singular;
    }

    public boolean hasPlural()
    {
        return this.plural != null;
    }

    public String getPlural()
    {
        return this.plural;
    }

    public Set<Occurrence> getOccurrences()
    {
        return occurrences;
    }

    public int compareTo(TranslatableMessage o)
    {
        if (this.position == null)
        {
            if (o.position == null)
            {
                int comp = this.singular.compareTo(o.singular);
                if (comp == 0)
                {
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
                }
                return comp;
            }
            return 1;
        }
        else if (o.position == null)
        {
            return -1;
        }
        return this.position.compareTo(o.position);
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

        TranslatableMessage that = (TranslatableMessage) o;

        if (occurrences != null ? !occurrences.equals(that.occurrences) : that.occurrences != null)
        {
            return false;
        }
        if (!plural.equals(that.plural))
        {
            return false;
        }
        if (!singular.equals(that.singular))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = singular.hashCode();
        result = 31 * result + plural.hashCode();
        result = 31 * result + (occurrences != null ? occurrences.hashCode() : 0);
        return result;
    }
}
