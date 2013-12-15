package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.IOException;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;

public interface CatalogFormat
{
    void write(MessageCatalog messageCatalog, CatalogConfiguration config, TranslatableMessageManager manager) throws IOException;
    TranslatableMessageManager read(MessageCatalog messageCatalog, CatalogConfiguration config) throws IOException;
    Class<? extends CatalogConfiguration> getConfigClass();
    String getFileExtension();
    void init(Logger logger);
}
