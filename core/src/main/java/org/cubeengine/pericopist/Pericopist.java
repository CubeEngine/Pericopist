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
package org.cubeengine.pericopist;

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

import org.cubeengine.pericopist.exception.CatalogFormatException;
import org.cubeengine.pericopist.exception.PericopistException;
import org.cubeengine.pericopist.exception.MessageExtractionException;
import org.cubeengine.pericopist.extractor.ExtractorConfiguration;
import org.cubeengine.pericopist.extractor.MessageExtractor;
import org.cubeengine.pericopist.format.CatalogConfiguration;
import org.cubeengine.pericopist.format.CatalogFormat;
import org.cubeengine.pericopist.message.MessageStore;

/**
 * This class is the main class of the project. It shall be used to generate or update a message catalog
 */
@SuppressWarnings("unused")
public class Pericopist
{
    private final Logger logger;

    private final ExtractorConfiguration extractorConfiguration;
    private final CatalogConfiguration catalogConfiguration;

    private final MessageExtractor messageExtractor;
    private final CatalogFormat catalogFormat;

    /**
     * The constructor creates a new pericopist instance
     *
     * @param extractorConfiguration configuration of the extractor
     * @param catalogConfiguration   configuration of the catalog
     *
     * @throws PericopistException if the {@link org.cubeengine.pericopist.extractor.MessageExtractor} or the {@link org.cubeengine.pericopist.format.CatalogFormat} couldn't be created.
     */
    public Pericopist(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration) throws PericopistException
    {
        this(extractorConfiguration, catalogConfiguration, null);
    }

    /**
     * The constructor creates a new pericopist instance
     *
     * @param extractorConfiguration configuration of the extractor
     * @param catalogConfiguration   configuration of the catalog
     * @param logger                 logger which shall be used
     *
     * @throws PericopistException if the {@link org.cubeengine.pericopist.extractor.MessageExtractor} or the {@link org.cubeengine.pericopist.format.CatalogFormat} couldn't be created.
     */
    public Pericopist(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration, Logger logger) throws PericopistException
    {
        this.logger = logger == null ? Logger.getLogger("pericopist") : logger;

        this.extractorConfiguration = extractorConfiguration;
        this.catalogConfiguration = catalogConfiguration;

        this.extractorConfiguration.validate();
        this.catalogConfiguration.validate();

        try
        {
            this.messageExtractor = extractorConfiguration.getExtractorClass().getConstructor().newInstance();
            this.messageExtractor.setLogger(this.logger);
        }
        catch (Exception e)
        {
            throw new PericopistException("Could not create a MessageExtractor instance of '" + extractorConfiguration.getExtractorClass().getName() + "'.", e);
        }

        try
        {
            this.catalogFormat = catalogConfiguration.getCatalogFormatClass().getConstructor().newInstance();
            this.catalogFormat.setLogger(this.logger);
        }
        catch (Exception e)
        {
            throw new PericopistException("Could not create a CatalogFormat instance of '" + catalogConfiguration.getCatalogFormatClass().getName() + "'.", e);
        }
    }

    /**
     * This method returns the {@link org.cubeengine.pericopist.extractor.ExtractorConfiguration} which is used by this class
     *
     * @return {@link org.cubeengine.pericopist.extractor.ExtractorConfiguration}
     */
    public ExtractorConfiguration getExtractorConfiguration()
    {
        return this.extractorConfiguration;
    }

    /**
     * This method returns the {@link org.cubeengine.pericopist.extractor.MessageExtractor} which is used by this class
     *
     * @return {@link org.cubeengine.pericopist.extractor.MessageExtractor}
     */
    public MessageExtractor getMessageExtractor()
    {
        return this.messageExtractor;
    }

    /**
     * This method returns the {@link org.cubeengine.pericopist.format.CatalogConfiguration} which is used by this class
     *
     * @return {@link org.cubeengine.pericopist.format.CatalogConfiguration}
     */
    public CatalogConfiguration getCatalogConfiguration()
    {
        return this.catalogConfiguration;
    }

    /**
     * This method returns the {@link org.cubeengine.pericopist.format.CatalogConfiguration} which is used by this class
     *
     * @return {@link org.cubeengine.pericopist.format.CatalogFormat}
     */
    public CatalogFormat getCatalogFormat()
    {
        return this.catalogFormat;
    }

    /**
     * This method generates a completely new message catalog. It overrides the existing file without to read it.
     *
     * @throws PericopistException if the extraction of the message or the creation of the new message catalog fails
     */
    public void generateCatalog() throws PericopistException
    {
        this.generateCatalog(new MessageStore());
    }

    /**
     * This method generates a new message catalog using the specified {@link org.cubeengine.pericopist.message.MessageStore} to handle the messages
     *
     * @param messageStore message store which shall store the messages
     *
     * @throws PericopistException if the extraction of the message or the creation of the new message catalog fails
     */
    private void generateCatalog(final MessageStore messageStore) throws PericopistException
    {
        this.createCatalog(this.parseSourceCode(messageStore));
    }

    /**
     * This method updates a message catalog. It reads the existing file if it exists and extracts the new messages from the project
     * with a {@link org.cubeengine.pericopist.message.MessageStore} containing the messages from the old catalog.
     *
     * @throws PericopistException if the extraction of the message or the creation of the new message catalog fails
     */
    public void updateCatalog() throws PericopistException
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
     * This method extracts the messages from the project using the {@link org.cubeengine.pericopist.extractor.MessageExtractor}.
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
     * @throws PericopistException if the reading fails
     */
    private MessageStore readCatalog() throws PericopistException
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
            throw new PericopistException("Couldn't read the catalog.", e);
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
     * @throws PericopistException if the creating fails.
     */
    private void createCatalog(MessageStore messageStore) throws PericopistException
    {
        Path tempPath;

        try
        {
            tempPath = Files.createTempFile("messageextractor.", ".tmp");
            tempPath.toFile().deleteOnExit();
        }
        catch (IOException e)
        {
            throw new PericopistException("The temporary file couldn't be created.", e);
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
            throw new PericopistException("The temp file couldn't be moved to the specified place.", e);
        }
    }
}
