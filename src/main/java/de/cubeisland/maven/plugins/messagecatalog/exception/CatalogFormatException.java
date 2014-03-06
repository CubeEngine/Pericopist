package de.cubeisland.maven.plugins.messagecatalog.exception;

public class CatalogFormatException extends MessageCatalogException
{
    public CatalogFormatException(String msg, Throwable t)
    {
        super(msg, t);
    }

    public CatalogFormatException(String msg)
    {
        super(msg);
    }
}
