package de.cubeisland.maven.plugins.messageextractor.exception;

public class SourceDirectoryNotExistsException extends MessageExtractorException
{
    public SourceDirectoryNotExistsException()
    {
        super("The source directory does not exist.");
    }
}
