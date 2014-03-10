package de.cubeisland.maven.plugins.messageextractor.exception;

public class MessageExtractionException extends MessageCatalogException
{
    public MessageExtractionException(String msg)
    {
        super(msg);
    }

    public MessageExtractionException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
