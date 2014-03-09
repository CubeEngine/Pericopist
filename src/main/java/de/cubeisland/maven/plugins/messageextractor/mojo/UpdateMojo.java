package de.cubeisland.maven.plugins.messageextractor.mojo;

import de.cubeisland.maven.plugins.messageextractor.MessageCatalog;
import de.cubeisland.maven.plugins.messageextractor.exception.MessageCatalogException;

/**
 * @goal update
 */
public class UpdateMojo extends AbstractMessageExtractorMojo
{
    @Override
    protected void doExecute(MessageCatalog catalog) throws MessageCatalogException
    {
        catalog.updateCatalog();
    }
}
