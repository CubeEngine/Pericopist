package de.cubeisland.maven.plugins.messagecatalog.format;

import org.apache.maven.plugin.logging.Log;

import de.cubeisland.maven.plugins.messagecatalog.format.gettext.PlaintextGettextCatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.config.Config;

public class CatalogFormatFactory
{
    public static CatalogFormat newCatalogFormat(String name, Config config, Log log) throws UnknownCatalogFormatException
    {
        if (name.equalsIgnoreCase("gettext"))
        {
            return new PlaintextGettextCatalogFormat(config, log);
        }
        throw new UnknownCatalogFormatException("Unknown format: " + name);
    }
}
