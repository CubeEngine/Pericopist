package de.cubeisland.maven.plugins.messagecatalog.parser;

import org.apache.maven.plugin.MojoFailureException;

public class UnknownSourceLanguageException extends MojoFailureException
{
    public UnknownSourceLanguageException(String message)
    {
        super(message);
    }
}
