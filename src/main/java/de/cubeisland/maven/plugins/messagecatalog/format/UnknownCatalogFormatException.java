package de.cubeisland.maven.plugins.messagecatalog.format;

import org.apache.maven.plugin.MojoFailureException;

public class UnknownCatalogFormatException extends MojoFailureException
{
    public UnknownCatalogFormatException(Object source, String shortMessage, String longMessage)
    {
        super(source, shortMessage, longMessage);
    }

    public UnknownCatalogFormatException(String message, Exception cause)
    {
        super(message, cause);
    }

    public UnknownCatalogFormatException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnknownCatalogFormatException(String message)
    {
        super(message);
    }
}
