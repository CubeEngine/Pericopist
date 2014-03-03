package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.IOException;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;

public interface CatalogFormat
{
    void write(MessageCatalog messageCatalog, CatalogConfiguration config, MessageStore manager) throws IOException;
    MessageStore read(MessageCatalog messageCatalog, CatalogConfiguration config) throws IOException;
    Class<? extends CatalogConfiguration> getConfigClass();
    String getFileExtension();
}
