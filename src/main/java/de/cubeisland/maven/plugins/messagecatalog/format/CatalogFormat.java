package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.File;
import java.io.IOException;

import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;

public interface CatalogFormat
{
    void write(File file, TranslatableMessageManager manager) throws IOException;
    TranslatableMessageManager read(File file) throws IOException;
    String getFileExtension();
}
