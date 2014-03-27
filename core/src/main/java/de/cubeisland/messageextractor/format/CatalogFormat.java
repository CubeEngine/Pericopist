/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Phillip Schichtel, Stefan Wolf
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
package de.cubeisland.messageextractor.format;

import org.apache.velocity.context.Context;

import de.cubeisland.messageextractor.exception.CatalogFormatException;
import de.cubeisland.messageextractor.message.MessageStore;

/**
 * This class is needed to read from or rather write a catalog.
 * It creates a message catalog which contains all messages from a message store.
 * Furthermore it's able to store the messages of an old catalog in a message store by reading it.
 *
 * @see de.cubeisland.messageextractor.extractor.MessageExtractor
 */
public interface CatalogFormat
{
    /**
     * This method writes the catalog file.
     *
     * @param config          config which shall be used to write the catalog
     * @param velocityContext a velocity context which can be used to update a string value
     * @param messageStore    the message store containing the messages for the catalog
     *
     * @throws CatalogFormatException
     */
    void write(CatalogConfiguration config, Context velocityContext, MessageStore messageStore) throws CatalogFormatException;

    /**
     * This method reads the catalog file and returns a message store containing the messages.
     * This method never returns null and implementations have to ensure this.
     *
     * @param config config which shall be used to read the catalog
     *
     * @return a MessageCatalog instance holding all messages
     *
     * @throws CatalogFormatException
     */
    MessageStore read(CatalogConfiguration config) throws CatalogFormatException;
}
