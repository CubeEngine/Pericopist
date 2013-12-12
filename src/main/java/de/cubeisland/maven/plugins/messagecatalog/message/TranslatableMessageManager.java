package de.cubeisland.maven.plugins.messagecatalog.message;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class TranslatableMessageManager implements Iterable<TranslatableMessage>
{
    private Set<TranslatableMessage> messages;

    public TranslatableMessageManager()
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
            if (message.getSingular().equals(singular) && (plural == null && !message.hasPlural() || message.hasPlural() && message.getPlural().equals(plural)))
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
