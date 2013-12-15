package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;

public interface SourceParser
{
    TranslatableMessageManager parse(MessageCatalog messageCatalog, SourceConfiguration config, TranslatableMessageManager manager);
    Class<? extends SourceConfiguration> getConfigClass();
    void init(Logger logger);
}
