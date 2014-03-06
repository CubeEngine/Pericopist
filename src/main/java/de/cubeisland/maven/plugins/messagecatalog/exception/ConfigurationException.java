package de.cubeisland.maven.plugins.messagecatalog.exception;

public class ConfigurationException extends MessageCatalogException
{
    public ConfigurationException(String msg)
    {
        super(msg);
    }

    public ConfigurationException(Throwable t)
    {
        super(t);
    }

    public ConfigurationException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
