package de.cubeisland.maven.plugins.messagecatalog.format;

import org.apache.velocity.context.Context;

import de.cubeisland.maven.plugins.messagecatalog.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;

public interface CatalogFormat
{
    void write(CatalogConfiguration config, Context velocityContext, MessageStore messageStore) throws CatalogFormatException;

    /**
     * Reads in the catalog file and returns a message store.
     * This method never returns null and implementations have to ensure this.
     *
     * @param config
     *
     * @return a MessageCatalog instance holding all messages
     *
     * @throws CatalogFormatException
     */
    MessageStore read(CatalogConfiguration config) throws CatalogFormatException;

    Class<? extends CatalogConfiguration> getConfigClass();
}
