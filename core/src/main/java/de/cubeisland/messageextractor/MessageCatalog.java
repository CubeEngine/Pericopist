/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

/**
 * This class is the main class of the project. It shall be used to generate or update message catalog
 */
public class MessageCatalog
{
    private final Logger logger;

    private final ExtractorConfiguration extractorConfiguration;
    private final CatalogConfiguration catalogConfiguration;

    private final MessageExtractor messageExtractor;
    private final CatalogFormat catalogFormat;

    /**
     * The constructor creates a new MessageCatalog instance
     *
     * @param extractorConfiguration configuration of the extractor
     * @param catalogConfiguration   configuration of the catalog
     *
     * @throws MessageCatalogException if the {@link de.cubeisland.messageextractor.extractor.MessageExtractor} or the {@link de.cubeisland.messageextractor.format.CatalogFormat} couldn't be created.
     */
    public MessageCatalog(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration) throws MessageCatalogException
    {
        this(extractorConfiguration, catalogConfiguration, null);
    }

    /**
     * The constructor creates a new MessageCatalog instance
     *
     * @param extractorConfiguration configuration of the extractor
     * @param catalogConfiguration   configuration of the catalog
     * @param logger                 logger which shall be used
     *
     * @throws MessageCatalogException if the {@link de.cubeisland.messageextractor.extractor.MessageExtractor} or the {@link de.cubeisland.messageextractor.format.CatalogFormat} couldn't be created.
     */
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

    /**
     * This method returns the {@link de.cubeisland.messageextractor.extractor.ExtractorConfiguration} which is used by this class
     *
     * @return {@link de.cubeisland.messageextractor.extractor.ExtractorConfiguration}
     */
    public ExtractorConfiguration getExtractorConfiguration()
    {
        return this.extractorConfiguration;
    }

    /**
     * This method returns the {@link de.cubeisland.messageextractor.extractor.MessageExtractor} which is used by this class
     *
     * @return {@link de.cubeisland.messageextractor.extractor.MessageExtractor}
     */
    public MessageExtractor getMessageExtractor()
    {
        return this.messageExtractor;
    }

    /**
     * This method returns the {@link de.cubeisland.messageextractor.format.CatalogConfiguration} which is used by this class
     *
     * @return {@link de.cubeisland.messageextractor.format.CatalogConfiguration}
     */
    public CatalogConfiguration getCatalogConfiguration()
    {
        return this.catalogConfiguration;
    }

    /**
     * This method returns the {@link de.cubeisland.messageextractor.format.CatalogConfiguration} which is used by this class
     *
     * @return {@link de.cubeisland.messageextractor.format.CatalogFormat}
     */
    public CatalogFormat getCatalogFormat()
    {
        return this.catalogFormat;
    }

    /**
     * This method generates a completely new message catalog. It overrides the existing file without to read it.
     *
     * @throws MessageCatalogException
     */
    public void generateCatalog() throws MessageCatalogException
    {
        this.generateCatalog(new MessageStore());
    }

    /**
     * This method generates a new message catalog using the specified {@link de.cubeisland.messageextractor.message.MessageStore} to handle the messages
     *
     * @param messageStore message store which shall store the messages
     *
     * @throws MessageCatalogException if the extraction of the message or the creation of the new message catalog fails
     */
    private void generateCatalog(final MessageStore messageStore) throws MessageCatalogException
    {
        this.createCatalog(this.parseSourceCode(messageStore));
    }

    /**
     * This method updates a message catalog. It reads the existing file if it exists and extracts the new messages from the project
     * with a {@link de.cubeisland.messageextractor.message.MessageStore} containing the messages from the old catalog.
     *
     * @throws MessageCatalogException
     */
    public void updateCatalog() throws MessageCatalogException
    {
        MessageStore messageStore = null;
        if (this.catalogConfiguration.getTemplateFile().exists())
        {
            messageStore = this.readCatalog();
        }
        if (messageStore == null)
        {
            messageStore = new MessageStore();
        }
        this.generateCatalog(messageStore);
    }

    /**
     * This method extracts the messages from the project using the {@link de.cubeisland.messageextractor.extractor.MessageExtractor}.
     * It stores in the messages in the specified message store and returns the same.
     *
     * @param messageStore message store which shall store the messages
     *
     * @return the message store which was specified
     *
     * @throws MessageExtractionException if the extraction failed
     */
    private MessageStore parseSourceCode(MessageStore messageStore) throws MessageExtractionException
    {
        return this.messageExtractor.extract(this.extractorConfiguration, messageStore);
    }

    /**
     * This method reads the messages of a message catalog.
     * It creates a {@link java.io.FileInputStream} and locks it for other accesses, reads the messages and releases the file.
     *
     * @return a message store containing the messages which were read from the message catalog
     *
     * @throws MessageCatalogException if the reading fails
     */
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

                if (channel.isOpen())
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

    /**
     * This method creates a new message catalog containing the messages from the specified message store.
     * <p/>
     * Therefore it creates a new temporary file and writes all the data in that file. After the file was written successfully,
     * it will be moved to the specified location and replaces the existing message catalog.
     *
     * @param messageStore message store containing all the messages which shall be included in the catalog
     *
     * @throws MessageCatalogException if the creating fails.
     */
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
            throw new MessageCatalogException("The temporary file couldn't be created.", e);
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
