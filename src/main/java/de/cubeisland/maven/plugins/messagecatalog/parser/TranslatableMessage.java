package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.util.Set;
import java.util.TreeSet;

public class TranslatableMessage implements Comparable<TranslatableMessage>
{
    private final String message;
    private final Set<Occurrence> occurrences;

    public TranslatableMessage(String message, Occurrence firstOccurrence)
    {
        this.message = message;
        this.occurrences = new TreeSet<Occurrence>();
        this.occurrences.add(firstOccurrence);
    }

    public void addOccurrence(Occurrence occurrence)
    {
        this.occurrences.add(occurrence);
    }

    public String getMessage()
    {
        return message;
    }

    public Set<Occurrence> getOccurrences()
    {
        return occurrences;
    }

    public int compareTo(TranslatableMessage o)
    {
        return this.message.toLowerCase().compareTo(o.getMessage().toLowerCase());
    }
}
