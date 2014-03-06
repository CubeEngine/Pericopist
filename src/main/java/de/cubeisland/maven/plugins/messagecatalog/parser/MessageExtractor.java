package de.cubeisland.maven.plugins.messagecatalog.parser;

import de.cubeisland.maven.plugins.messagecatalog.exception.MessageExtractorException;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;

public interface MessageExtractor
{
    MessageStore extract(ExtractorConfiguration config) throws MessageExtractorException;
    MessageStore extract(ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractorException;
    Class<? extends ExtractorConfiguration> getConfigClass();
}
