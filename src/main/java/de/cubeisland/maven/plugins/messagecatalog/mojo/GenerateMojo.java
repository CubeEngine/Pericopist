package de.cubeisland.maven.plugins.messagecatalog.mojo;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageCatalogException;

/**
 * Blabla
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMessageCatalogMojo
{
    @Override
    public void doExecute(MessageCatalog catalog) throws MessageCatalogException
    {
        catalog.generateCatalog();
    }
}
