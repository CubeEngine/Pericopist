package de.cubeisland.maven.plugins.messagecatalog.parser;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;

public interface SourceParser
{
    MessageStore parse(MessageCatalog messageCatalog, SourceConfiguration config);
    MessageStore parse(MessageCatalog messageCatalog, SourceConfiguration config, MessageStore manager);
    Class<? extends SourceConfiguration> getConfigClass();
}
