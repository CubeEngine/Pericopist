package de.cubeisland.maven.plugins.messagecatalog.format;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;

public interface CatalogFormat
{
    void write(MessageCatalog messageCatalog, CatalogConfiguration config, MessageStore manager) throws CatalogFormatException;
    MessageStore read(MessageCatalog messageCatalog, CatalogConfiguration config) throws CatalogFormatException;
    Class<? extends CatalogConfiguration> getConfigClass();
    String getFileExtension();
}
