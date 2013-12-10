package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.io.File;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;

public interface SourceParser
{
    TranslatableMessageManager parse(File sourceDirectory, TranslatableMessageManager manager);
}
