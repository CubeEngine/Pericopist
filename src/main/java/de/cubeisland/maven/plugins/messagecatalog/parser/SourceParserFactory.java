package de.cubeisland.maven.plugins.messagecatalog.parser;

import org.apache.maven.plugin.logging.Log;

import java.util.Map;

import de.cubeisland.maven.plugins.messagecatalog.parser.java.JavaSourceParser;

public class SourceParserFactory
{
    public static SourceParser newSourceParser(String language, Map<String, Object> config, Log log) throws UnknownSourceLanguageException
    {
        SourceParser parser;
        if (language.equalsIgnoreCase("java"))
        {
            parser = new JavaSourceParser(config, log);
        }
        else
        {
            throw new UnknownSourceLanguageException("Unknown source language: " + language);
        }

        if (config.containsKey("basePackage"))
        {
            parser.setBasePackage(String.valueOf(config.get("basePackage")));
        }
        else
        {
            parser.setBasePackage("de.cubeisland.cubeengine");
        }

        return parser;
    }
}
