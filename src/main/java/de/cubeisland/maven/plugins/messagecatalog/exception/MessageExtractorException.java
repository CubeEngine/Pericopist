package de.cubeisland.maven.plugins.messagecatalog.exception;

public class MessageExtractorException extends MessageCatalogException
{
    public MessageExtractorException(String msg)
    {
        super(msg);
    }

    public MessageExtractorException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
