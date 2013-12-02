package de.cubeisland.maven.plugins.messagecatalog.message;

import java.util.Set;
import java.util.TreeSet;

public class TranslatableMessage implements Comparable<TranslatableMessage>
{
    private final String singular;
    private final String plural;
    private final Set<Occurrence> occurrences;

    public final Integer position;

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
        if(firstOccurrence != null)
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
        if(this.position == null)
        {
            if(o.position == null)
            {
                int comp = this.singular.compareTo(o.singular);
                if(comp == 0)
                {
                    if(this.hasPlural())
                    {
                        if(o.hasPlural())
                        {
                            return this.plural.compareTo(o.plural);
                        }
                        return 1;
                    }
                    else if(o.hasPlural())
                    {
                        return -1;
                    }
                }
                return comp;
            }
            return 1;
        }
        else if(o.position == null)
        {
            return -1;
        }
        return Integer.compare(this.position, o.position);
    }
}
