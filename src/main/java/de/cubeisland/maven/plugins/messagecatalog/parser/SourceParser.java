package de.cubeisland.maven.plugins.messagecatalog.parser;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.exception.SourceParserException;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;

public interface SourceParser
{
    MessageStore parse(MessageCatalog messageCatalog, SourceConfiguration config) throws SourceParserException;
    MessageStore parse(MessageCatalog messageCatalog, SourceConfiguration config, MessageStore manager) throws SourceParserException;
    Class<? extends SourceConfiguration> getConfigClass();
}
