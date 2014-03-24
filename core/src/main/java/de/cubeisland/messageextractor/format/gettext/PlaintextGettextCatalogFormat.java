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
package de.cubeisland.messageextractor.format.gettext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Logger;

import de.cubeisland.messageextractor.exception.CatalogFormatException;
import de.cubeisland.messageextractor.format.CatalogConfiguration;
import de.cubeisland.messageextractor.format.CatalogFormat;
import de.cubeisland.messageextractor.format.HeaderSection;
import de.cubeisland.messageextractor.format.HeaderSection.MetadataEntry;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.Occurrence;
import de.cubeisland.messageextractor.message.TranslatableMessage;
import org.apache.velocity.context.Context;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private Catalog oldCatalog;

    private Logger logger;

    public void write(CatalogConfiguration config, Charset charset, Context velocityContext, MessageStore messageStore) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration)config;
        Catalog catalog = new Catalog(true);

        if (this.logger == null)
        {
            this.logger = Logger.getLogger("messageextractor");
        }

        for (TranslatableMessage translatableMessage : messageStore)
        {
            if (translatableMessage.getOccurrences().isEmpty())
            {
                if (catalogConfig.getRemoveUnusedMessages())
                {
                    continue;
                }
                else
                {
                    this.logger.info("message with msgid '" + translatableMessage.getSingular() + "' does not occur!");
                }
            }
            Message message = new Message();
            for (Occurrence occurrence : translatableMessage.getOccurrences())
            {
                message.addSourceReference(occurrence.getPath(), occurrence.getLine());
            }
            message.setMsgid(translatableMessage.getSingular());
            if (translatableMessage.hasPlural())
            {
                message.setMsgidPlural(translatableMessage.getPlural());
            }

            catalog.addMessage(message);
        }

        if (catalogConfig.getHeaderSection() != null)
        {
            try
            {
                catalog.addMessage(this.getHeaderMessage(catalogConfig.getHeaderSection(), velocityContext));
            }
            catch (IOException e)
            {
                throw new CatalogFormatException("The header could not be created.", e);
            }
        }

        if (this.compareCatalogs(this.oldCatalog, catalog, catalogConfig.getHeaderSection()))
        {
            this.logger.info("Did not create a new catalog, because it's the same like the old one.");
            return;
        }
        final File template = catalogConfig.getTemplateFile();

        if (template.exists() && !template.delete())
        {
            throw new CatalogFormatException("The old template could not be deleted.");
        }

        int messageCount = catalog.size();
        if (catalogConfig.getHeaderSection() != null)
        {
            messageCount--;
        }
        if (messageCount == 0 && !catalogConfig.getCreateEmptyTemplate())
        {
            this.logger.info("The project does not contain any translatable message. The template was not created.");
            return;
        }

        final PoWriter poWriter = new PoWriter(true);
        try
        {
            final File directory = template.getParentFile();
            if (directory.exists() || directory.mkdirs())
            {
                poWriter.write(catalog, new FileOutputStream(template), charset);
            }
            else
            {
                throw new CatalogFormatException("Failed to create the directory '" + directory
                    .getAbsolutePath() + "'!");
            }
        }
        catch (IOException e)
        {
            throw new CatalogFormatException("The catalog could not be created", e);
        }
    }

    public MessageStore read(CatalogConfiguration config, Charset charset) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration)config;
        MessageStore messageStore = new MessageStore();

        Catalog catalog = new Catalog(true);
        PoParser poParser = new PoParser(catalog);
        try
        {
            catalog = poParser.parseCatalog(new FileInputStream(catalogConfig.getTemplateFile()), charset, true);
        }
        catch (IOException e)
        {
            throw new CatalogFormatException("The catalog could not be read.", e);
        }

        this.oldCatalog = catalog;

        int i = 0;
        for (Message message : catalog)
        {
            if (!message.isHeader())
            {
                messageStore.addMessage(message.getMsgid(), message.getMsgidPlural(), i++);
            }
        }

        return messageStore;
    }

    public Class<? extends CatalogConfiguration> getConfigClass()
    {
        return GettextCatalogConfiguration.class;
    }

    private Message getHeaderMessage(HeaderSection config, Context velocityContext) throws IOException
    {
        HeaderFields headerFields = new HeaderFields();
        if (config.getMetadata() != null)
        {
            for (MetadataEntry entry : config.getMetadata())
            {
                headerFields.setValue(entry.getKey(), entry.getValue());
            }
        }

        Message headerMessage = headerFields.unwrap();

        if (config.getCommentsResource() != null)
        {
            for (String comment : config.getComments(velocityContext).split("\n"))
            {
                headerMessage.addComment(comment);
            }
        }

        return headerMessage;
    }

    /**
     * This method compares the messages of the current catalog with the messages from the new one
     *
     * @param old        the current catalog
     * @param newCatalog the new catalog
     *
     * @return returns whether the new catalog contains the same messages at the same place as the old one
     */
    private boolean compareCatalogMessages(Catalog old, Catalog newCatalog)
    {
        for (Message firstMessage : old)
        {
            if (firstMessage.isHeader())
            {
                continue;
            }

            Message secondMessage = newCatalog.locateMessage(firstMessage.getMsgctxt(), firstMessage.getMsgid());

            if (secondMessage == null)
            {
                return false;
            }

            List<String> firstSourceReferences = firstMessage.getSourceReferences();
            List<String> secondSourceReferences = secondMessage.getSourceReferences();

            if (firstSourceReferences.size() != secondSourceReferences.size())
            {
                return false;
            }

            for (String firstSourceReference : firstSourceReferences)
            {
                if (!secondSourceReferences.contains(firstSourceReference))
                {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method compares the header of the old catalog with the header of the new one.
     *
     * @param old           the old catalog
     * @param newHeader     the new catalog
     * @param headerSection the header configuration
     *
     * @return returns whether the new header of the catalog is the same as the old one
     */
    private boolean compareCatalogHeader(Message old, Message newHeader, HeaderSection headerSection)
    {
        if (old == null || newHeader == null)
        {
            return false;
        }
        if (old.getComments().size() != newHeader.getComments().size())
        {
            return false;
        }

        String[] firstComments = old.getComments().toArray(new String[old.getComments().size()]);
        String[] secondComments = newHeader.getComments().toArray(new String[firstComments.length]);

        for (int i = 0; i < firstComments.length; i++)
        {
            if (!firstComments[i].equals(secondComments[i]))
            {
                return false;
            }
        }

        String[] fields = old.getMsgstr().split("\n");
        if (fields.length != headerSection.getMetadata().size())
        {
            return false;
        }

        for (int i = 0; i < fields.length; i++)
        {
            String[] parts = fields[i].split(":");
            MetadataEntry entry = headerSection.getMetadata().get(i);

            if (!parts[0].equals(entry.getKey()))
            {
                return false;
            }

            if (!entry.isVariable())
            {
                StringBuilder value = new StringBuilder(parts[1].substring(1));

                for (int j = 2; j < parts.length; j++)
                {
                    value.append(":");
                    value.append(parts[j]);
                }

                if (!value.toString().equals(entry.getValue()))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * This method compares the current catalog with a new one
     *
     * @param old           the current catalog
     * @param newCatalog    the new catalog
     * @param headerSection the header configuration of the new catalog
     *
     * @return returns whether the new catalog is the same as the old one
     */
    private boolean compareCatalogs(Catalog old, Catalog newCatalog, HeaderSection headerSection)
    {
        if (old == null || newCatalog == null)
        {
            return false;
        }
        if (old.size() != newCatalog.size())
        {
            return false;
        }
        if (!this.compareCatalogMessages(old, newCatalog))
        {
            return false;
        }

        return this.compareCatalogHeader(old.locateHeader(), newCatalog.locateHeader(), headerSection);
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }
}
