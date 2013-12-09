package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.io.File;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;

public interface SourceParser
{
    Set<TranslatableMessage> parse(File sourceDirectory);
}
