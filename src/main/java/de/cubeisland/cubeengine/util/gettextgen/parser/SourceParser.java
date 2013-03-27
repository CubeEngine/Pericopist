package de.cubeisland.cubeengine.util.gettextgen.parser;

import java.io.File;
import java.util.Set;

public interface SourceParser
{
    Set<TranslatableMessage> parse(File sourceDirectory);
}
