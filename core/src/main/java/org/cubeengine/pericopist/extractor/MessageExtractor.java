/*
 * The MIT License
 * Copyright © 2013 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.pericopist.extractor;

import java.util.logging.Logger;

import org.cubeengine.pericopist.exception.MessageExtractionException;
import org.cubeengine.pericopist.message.MessageStore;

/**
 * This class is used to extract all the messages which shall be translated from a complete project.
 * The messages will be stored in a message store which can be processed with a catalog format.
 *
 * @see org.cubeengine.pericopist.format.CatalogFormat
 */
public interface MessageExtractor
{
    /**
     * This method scans the source directory and extracts the translatable messages of the source files.
     *
     * @param config       the config which shall be used to extract the messages
     * @param messageStore a message store containing the messages from the old catalog
     *
     * @return a message store containing all the messages
     *
     * @throws MessageExtractionException if the extraction process fails
     */
    MessageStore extract(ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractionException;

    /**
     * This method sets the logger which can be used by the MessageExtractor
     *
     * @param logger the logger
     */
    void setLogger(Logger logger);
}
