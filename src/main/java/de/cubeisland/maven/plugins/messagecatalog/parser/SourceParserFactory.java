package de.cubeisland.maven.plugins.messagecatalog.parser;

import org.apache.maven.plugin.logging.Log;

import java.util.Map;

import de.cubeisland.maven.plugins.messagecatalog.parser.java.JavaSourceParser;

public class SourceParserFactory
{
    public static SourceParser newSourceParser(String language, Map<String, Object> config, Log log) throws UnknownSourceLanguageException
    {
        if (language.equalsIgnoreCase("java"))
        {
            return new JavaSourceParser(config, log);
        }
        throw new UnknownSourceLanguageException("Unknown source language: " + language);
    }
}
