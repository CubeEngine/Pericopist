package de.cubeisland.maven.plugins.messageextractor.format;

import org.apache.velocity.context.Context;

import java.nio.charset.Charset;

import de.cubeisland.maven.plugins.messageextractor.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messageextractor.message.MessageStore;

public interface CatalogFormat
{
    void write(CatalogConfiguration config, Charset charset, Context velocityContext, MessageStore messageStore) throws CatalogFormatException;

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
    MessageStore read(CatalogConfiguration config, Charset charset) throws CatalogFormatException;

    Class<? extends CatalogConfiguration> getConfigClass();
}
