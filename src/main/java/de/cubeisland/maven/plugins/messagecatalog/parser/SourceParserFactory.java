package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.util.Map;

import de.cubeisland.maven.plugins.messagecatalog.parser.java.JavaSourceParser;
import de.cubeisland.maven.plugins.messagecatalog.util.Config;

import org.apache.maven.plugin.logging.Log;

public class SourceParserFactory
{
    public static SourceParser newSourceParser(String language, Config config, Log log) throws UnknownSourceLanguageException
    {
        if (language.equalsIgnoreCase("java"))
        {
            return new JavaSourceParser(config, log);
        }
        throw new UnknownSourceLanguageException("Unknown source language: " + language);
    }
}
