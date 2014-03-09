package de.cubeisland.maven.plugins.messageextractor.mojo;

import de.cubeisland.maven.plugins.messageextractor.MessageCatalog;
import de.cubeisland.maven.plugins.messageextractor.exception.MessageCatalogException;

/**
 * Blabla
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMessageExtractorMojo
{
    @Override
    public void doExecute(MessageCatalog catalog) throws MessageCatalogException
    {
        catalog.generateCatalog();
    }
}
