package de.cubeisland.maven.plugins.messagecatalog.mojo;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageCatalogException;

/**
 * @goal update
 */
public class UpdateMojo extends AbstractMessageCatalogMojo
{
    @Override
    protected void doExecute(MessageCatalog catalog) throws MessageCatalogException
    {
        catalog.updateCatalog();
    }
}
