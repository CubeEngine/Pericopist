package de.cubeisland.maven.plugins.messagecatalog.format;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;

public interface CatalogFormat
{
    void write(File file, Set<TranslatableMessage> messages) throws IOException;
    String getFileExtension();
}
