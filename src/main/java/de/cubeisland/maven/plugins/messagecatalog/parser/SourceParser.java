package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.io.File;

import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;

public interface SourceParser
{
    TranslatableMessageManager parse(File sourceDirectory, TranslatableMessageManager manager);
}
