package de.cubeisland.maven.plugins.messagecatalog.exception;

public class ConfigurationNotFoundException extends ConfigurationException
{
    public ConfigurationNotFoundException(String msg)
    {
        super(msg);
    }

    public ConfigurationNotFoundException(Throwable t)
    {
        super(t);
    }

    public ConfigurationNotFoundException(String msg, Throwable t)
    {
        super(msg, t);
    }
}
