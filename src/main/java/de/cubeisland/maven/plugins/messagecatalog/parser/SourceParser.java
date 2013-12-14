package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;

public interface SourceParser
{
    TranslatableMessageManager parse(SourceConfig config, TranslatableMessageManager manager);
    Class<? extends SourceConfig> getSourceConfigClass();
    void init(Logger logger);
}
