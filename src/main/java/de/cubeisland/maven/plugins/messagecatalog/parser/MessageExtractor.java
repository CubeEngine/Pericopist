package de.cubeisland.maven.plugins.messagecatalog.parser;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageExtractorException;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;

public interface MessageExtractor
{
    MessageStore parse(MessageCatalog messageCatalog, ExtractorConfiguration config) throws MessageExtractorException;
    MessageStore parse(MessageCatalog messageCatalog, ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractorException;
    Class<? extends ExtractorConfiguration> getConfigClass();
}
