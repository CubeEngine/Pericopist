package de.cubeisland.maven.plugins.messagecatalog.exception;

public class MessageCatalogException extends Exception
{
    public MessageCatalogException(String msg)
    {
        super(msg);
    }

    public MessageCatalogException(Throwable t)
    {
        super(t);
    }

    public MessageCatalogException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
