package de.cubeisland.cubeengine.util.gettextgen.format;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import de.cubeisland.cubeengine.util.gettextgen.parser.TranslatableMessage;

public interface CatalogFormat
{
    void write(File file, Set<TranslatableMessage> messages) throws IOException;
    String getFileExtension();
}
