package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.IOException;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;

public interface CatalogFormat
{
    void write(CatalogConfig config, TranslatableMessageManager manager) throws IOException;
    TranslatableMessageManager read(CatalogConfig config) throws IOException;
    Class<? extends CatalogConfig> getCatalogConfigClass();
    String getFileExtension();
    void init(Logger logger);
}
