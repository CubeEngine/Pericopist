package de.cubeisland.maven.plugins.messagecatalog.exception;

public class UnknownCatalogFormatException extends ConfigurationException
{
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
