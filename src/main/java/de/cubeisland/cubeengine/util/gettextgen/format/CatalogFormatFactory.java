package de.cubeisland.cubeengine.util.gettextgen.format;

import java.util.Map;

import de.cubeisland.cubeengine.util.gettextgen.format.gettext.PlaintextGettextCatalogFormat;

import org.apache.maven.plugin.logging.Log;

public class CatalogFormatFactory
{
    public static CatalogFormat newCatalogFormat(String name, Map<String, Object> config, Log log) throws UnknownCatalogFormatException
    {
        if (name.equalsIgnoreCase("gettext"))
        {
            return new PlaintextGettextCatalogFormat(config, log);
        }
        throw new UnknownCatalogFormatException("Unknown format: " + name);
    }
}
