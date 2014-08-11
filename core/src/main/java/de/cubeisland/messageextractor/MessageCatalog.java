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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final Logger logger;

    private final ExtractorConfiguration extractorConfiguration;
    private final CatalogConfiguration catalogConfiguration;

    private final MessageExtractor messageExtractor;
    private final CatalogFormat catalogFormat;

    public MessageCatalog(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration) throws MessageCatalogException
    {
        this(extractorConfiguration, catalogConfiguration, null);
    }

    public MessageCatalog(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration, Logger logger) throws MessageCatalogException
    {
        this.logger = logger == null ? Logger.getLogger("messageextractor") : logger;

        this.extractorConfiguration = extractorConfiguration;
        this.catalogConfiguration = catalogConfiguration;

        this.extractorConfiguration.validate();
        this.catalogConfiguration.validate();

        try
        {
            this.messageExtractor = extractorConfiguration.getExtractorClass().newInstance();
            this.messageExtractor.setLogger(this.logger);
        }
        catch (Exception e)
        {
            throw new MessageCatalogException("Could not create a MessageExtractor instance of '" + extractorConfiguration.getExtractorClass().getName() + "'.", e);
        }

        try
        {
            this.catalogFormat = catalogConfiguration.getCatalogFormatClass().newInstance();
            this.catalogFormat.setLogger(this.logger);
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

    private MessageStore parseSourceCode() throws MessageExtractionException
    {
        return this.messageExtractor.extract(this.extractorConfiguration);
    }

    private MessageStore parseSourceCode(MessageStore messageStore) throws MessageExtractionException
    {
        return this.messageExtractor.extract(this.extractorConfiguration, messageStore);
    }

    private MessageStore readCatalog() throws MessageCatalogException
    {
        MessageStore messageStore;
        try
        {
            try (FileInputStream fileInputStream = new FileInputStream(this.catalogConfiguration.getTemplateFile()))
            {
                FileChannel channel = fileInputStream.getChannel();
                FileLock lock = channel.lock(0L, Long.MAX_VALUE, true);

                messageStore = this.catalogFormat.read(this.catalogConfiguration, fileInputStream);

                if(channel.isOpen())
                {
                    lock.release();
                }
            }
        }
        catch (IOException e)
        {
            throw new MessageCatalogException("Couldn't read the catalog.", e);
        }

        return messageStore;
    }

    private void createCatalog(MessageStore messageStore) throws MessageCatalogException
    {
        Path tempPath;

        try
        {
            tempPath = Files.createTempFile("messageextractor.", ".tmp");
            tempPath.toFile().deleteOnExit();
        }
        catch (IOException e)
        {
            throw new MessageCatalogException("The temp file couldn't be created.", e);
        }

        boolean wroteFile = false;
        try
        {
            try (FileOutputStream outputStream = new FileOutputStream(tempPath.toFile()))
            {
                wroteFile = this.catalogFormat.write(this.catalogConfiguration, outputStream, messageStore);
            }
        }
        catch (IOException e)
        {
            this.logger.log(Level.SEVERE, "An error occurred while creating and handling the output stream of the temporary template file.", e);
        }

        if (!wroteFile)
        {
            try
            {
                Files.delete(tempPath);
            }
            catch (IOException e)
            {
                this.logger.log(Level.WARNING, "The temp file in path '" + tempPath + "' couldn't be deleted.", e);
            }
            return;
        }

        try
        {
            final File template = this.catalogConfiguration.getTemplateFile();
            final File directory = template.getParentFile();

            if (directory.exists() || directory.mkdirs())
            {
                Files.move(tempPath, this.catalogConfiguration.getTemplateFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            else
            {
                throw new CatalogFormatException("The directory of the template in '" + directory.getAbsolutePath() + "' couldn't be created.");
            }
        }
        catch (IOException e)
        {
            throw new MessageCatalogException("The temp file couldn't be moved to the specified place.", e);
        }
    }
}
