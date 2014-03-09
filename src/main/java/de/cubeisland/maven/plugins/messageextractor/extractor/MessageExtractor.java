package de.cubeisland.maven.plugins.messageextractor.extractor;

import de.cubeisland.maven.plugins.messageextractor.exception.MessageExtractorException;
import de.cubeisland.maven.plugins.messageextractor.message.MessageStore;

public interface MessageExtractor
{
    MessageStore extract(ExtractorConfiguration config) throws MessageExtractorException;
    MessageStore extract(ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractorException;
    Class<? extends ExtractorConfiguration> getConfigClass();
}
