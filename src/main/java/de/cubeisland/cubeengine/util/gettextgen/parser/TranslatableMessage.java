package de.cubeisland.cubeengine.util.gettextgen.parser;

import java.util.HashSet;
import java.util.Set;

public class TranslatableMessage
{
    private final String message;
    private final Set<Occurrence> occurrences;

    public TranslatableMessage(String message, Occurrence firstOccurrence)
    {
        this.message = message;
        this.occurrences = new HashSet<Occurrence>();
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
}
