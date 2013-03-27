package de.cubeisland.cubeengine.util.gettextgen.parser;

import java.util.Map;

import de.cubeisland.cubeengine.util.gettextgen.parser.java.JavaSourceParser;

import org.apache.maven.plugin.logging.Log;

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
