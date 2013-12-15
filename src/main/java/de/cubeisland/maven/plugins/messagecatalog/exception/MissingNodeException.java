package de.cubeisland.maven.plugins.messagecatalog.exception;

import de.cubeisland.maven.plugins.messagecatalog.Configuration;

public class MissingNodeException extends ConfigurationException
{
    private MissingNodeException(String msg)
    {
        super(msg);
    }

    public static MissingNodeException of(Configuration configuration, String field)
    {
        return new MissingNodeException("The configuration " + configuration.getClass().getName() + " needs the node <" + field + ">");
    }
}
