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
package de.cubeisland.messageextractor;

import org.apache.velocity.context.Context;

import de.cubeisland.messageextractor.exception.CatalogFormatException;
import de.cubeisland.messageextractor.exception.MessageCatalogException;
import de.cubeisland.messageextractor.exception.MessageExtractionException;
import de.cubeisland.messageextractor.extractor.ExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.MessageExtractor;
import de.cubeisland.messageextractor.format.CatalogConfiguration;
import de.cubeisland.messageextractor.format.CatalogFormat;
import de.cubeisland.messageextractor.message.MessageStore;

public class MessageCatalog
{
    private final Context context;

    private final ExtractorConfiguration extractorConfiguration;
    private final CatalogConfiguration catalogConfiguration;

    private final MessageExtractor messageExtractor;
    private final CatalogFormat catalogFormat;

    public MessageCatalog(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration) throws MessageCatalogException
    {
        this(extractorConfiguration, catalogConfiguration, null);
    }

    public MessageCatalog(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration, Context context) throws MessageCatalogException
    {
        this.extractorConfiguration = extractorConfiguration;
        this.catalogConfiguration = catalogConfiguration;

        this.extractorConfiguration.validateConfiguration();
        this.catalogConfiguration.validateConfiguration();

        this.context = context;

        try
        {
            this.messageExtractor = extractorConfiguration.getExtractorClass().newInstance();
        }
        catch (Exception e)
        {
            throw new MessageCatalogException("Could not create a MessageExtractor instance of '" + extractorConfiguration.getExtractorClass().getName() + "'.", e);
        }

        try
        {
            this.catalogFormat = catalogConfiguration.getCatalogFormatClass().newInstance();
        }
        catch (Exception e)
        {
            throw new MessageCatalogException("Could not create a CatalogFormat instance of '" + catalogConfiguration.getCatalogFormatClass().getName() + "'.", e);
        }
    }

    public ExtractorConfiguration getExtractorConfiguration()
    {
        return this.extractorConfiguration;
    }

    public MessageExtractor getMessageExtractor()
    {
        return this.messageExtractor;
    }

    public CatalogConfiguration getCatalogConfiguration()
    {
        return this.catalogConfiguration;
    }

    public CatalogFormat getCatalogFormat()
    {
        return this.catalogFormat;
    }

    public Context getVelocityContext()
    {
        return this.context;
    }

    public void generateCatalog() throws MessageCatalogException
    {
        this.createCatalog(this.parseSourceCode());
    }

    private void generateCatalog(final MessageStore messageStore) throws MessageCatalogException
    {
        this.createCatalog(this.parseSourceCode(messageStore));
    }

    public void updateCatalog() throws MessageCatalogException
    {
        MessageStore messageStore = null;
        if (this.catalogConfiguration.getTemplateFile().exists())
        {
            messageStore = this.readCatalog();
        }
        this.generateCatalog(messageStore);
    }

    private MessageStore readCatalog() throws CatalogFormatException
    {
        return this.catalogFormat.read(this.catalogConfiguration);
    }

    private MessageStore parseSourceCode() throws MessageExtractionException
    {
        return this.messageExtractor.extract(this.extractorConfiguration);
    }

    private MessageStore parseSourceCode(MessageStore messageStore) throws MessageExtractionException
    {
        return this.messageExtractor.extract(this.extractorConfiguration, messageStore);
    }

    private void createCatalog(MessageStore messageStore) throws CatalogFormatException
    {
        this.catalogFormat.write(this.catalogConfiguration, this.getVelocityContext(), messageStore);
    }
}
