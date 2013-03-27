package de.cubeisland.maven.messagecatalog.parser;

import java.io.File;
import java.util.Set;

public interface SourceParser
{
    Set<TranslatableMessage> parse(File sourceDirectory);
}
