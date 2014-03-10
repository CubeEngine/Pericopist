package de.cubeisland.maven.plugins.messageextractor.exception;

public class SourceDirectoryNotExistingException extends MessageExtractionException
{
    public SourceDirectoryNotExistingException()
    {
        super("The source directory does not exist.");
    }
}
