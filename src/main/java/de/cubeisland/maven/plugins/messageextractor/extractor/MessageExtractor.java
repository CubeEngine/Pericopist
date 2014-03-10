package de.cubeisland.maven.plugins.messageextractor.extractor;

import de.cubeisland.maven.plugins.messageextractor.exception.MessageExtractionException;
import de.cubeisland.maven.plugins.messageextractor.message.MessageStore;

public interface MessageExtractor
{
    MessageStore extract(ExtractorConfiguration config) throws MessageExtractionException;
    MessageStore extract(ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractionException;
    Class<? extends ExtractorConfiguration> getConfigClass();
}
