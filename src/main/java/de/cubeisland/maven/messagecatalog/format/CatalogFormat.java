package de.cubeisland.maven.messagecatalog.format;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import de.cubeisland.maven.messagecatalog.parser.TranslatableMessage;

public interface CatalogFormat
{
    void write(File file, Set<TranslatableMessage> messages) throws IOException;
    String getFileExtension();
}
