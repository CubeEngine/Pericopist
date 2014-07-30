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

import org.apache.velocity.context.Context;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.cubeisland.messageextractor.exception.CatalogFormatException;
import de.cubeisland.messageextractor.format.CatalogConfiguration;
import de.cubeisland.messageextractor.format.CatalogFormat;
import de.cubeisland.messageextractor.format.HeaderConfiguration;
import de.cubeisland.messageextractor.format.HeaderConfiguration.MetadataEntry;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.Occurrence;
import de.cubeisland.messageextractor.message.TranslatableMessage;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private Catalog oldCatalog;
    private Logger logger;

    /**
     * {@inheritDoc}
     *
     * @param config          config which shall be used to write the catalog
     * @param velocityContext a velocity context which can be used to update a string value
     * @param messageStore    the message store containing the messages for the catalog
     *
     * @throws CatalogFormatException
     */
    @Override
    public void write(CatalogConfiguration config, Context velocityContext, MessageStore messageStore) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration) config;

        Catalog catalog = this.getCatalog(catalogConfig, messageStore);

        if (catalogConfig.getHeaderConfiguration() != null)
        {
            try
            {
                catalog.addMessage(this.getHeaderMessage(catalogConfig.getHeaderConfiguration(), velocityContext));
            }
            catch (IOException e)
            {
                throw new CatalogFormatException("The header could not be created.", e);
            }
        }

        if (this.compareCatalogs(this.oldCatalog, catalog, catalogConfig.getHeaderConfiguration()))
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
        if (catalogConfig.getHeaderConfiguration() != null)
        {
            messageCount--;
        }
        if (messageCount == 0 && !catalogConfig.getCreateEmptyTemplate())
        {
            this.logger.info("The project does not contain any translatable message. The template was not created.");
            return;
        }

        this.writeCatalog(catalog, catalogConfig, template);
        this.logger.info("The " + this.getClass().getSimpleName() + " created a new template with " + messageCount + " messages.");
    }

    private void writeCatalog(Catalog catalog, GettextCatalogConfiguration configuration, File template) throws CatalogFormatException
    {
        final PoWriter poWriter = new PoWriter(true);
        try
        {
            final File directory = template.getParentFile();
            if (directory.exists() || directory.mkdirs())
            {
                FileOutputStream stream = new FileOutputStream(template);
                poWriter.write(catalog, stream, configuration.getCharset());
                stream.close();
            }
            else
            {
                throw new CatalogFormatException("Failed to create the directory '" + directory.getAbsolutePath() + "'!");
            }
        }
        catch (IOException e)
        {
            throw new CatalogFormatException("The catalog could not be created", e);
        }
    }

    private Catalog getCatalog(GettextCatalogConfiguration configuration, MessageStore messageStore)
    {
        Catalog catalog = new Catalog(true);

        for (TranslatableMessage translatableMessage : messageStore)
        {
            if (translatableMessage.getOccurrences().isEmpty())
            {
                if (configuration.getRemoveUnusedMessages())
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
                for (int i = 0; i < configuration.getPluralAmount(); i++)
                {
                    message.addMsgstrPlural("", i);
                }
            }

            this.setPreviousMessageIds(message);

            catalog.addMessage(message);
        }

        return catalog;
    }

    private void setPreviousMessageIds(Message message)
    {
        if (this.oldCatalog == null)
        {
            return;
        }

        Message obsoleteMessage = this.oldCatalog.locateMessage(message.getMsgctxt(), message.getMsgid());
        if (obsoleteMessage != null)
        {
            message.setPrevMsgctx(obsoleteMessage.getPrevMsgctx());
            message.setPrevMsgid(obsoleteMessage.getPrevMsgid());
            message.setPrevMsgidPlural(obsoleteMessage.getPrevMsgidPlural());

            if (message.isPlural() && !message.getMsgidPlural().equals(obsoleteMessage.getMsgidPlural()))
            {
                message.setPrevMsgidPlural(obsoleteMessage.getMsgidPlural());
            }

            return;
        }

        List<Message> messageList = new ArrayList<Message>(1);

        for (String reference : message.getSourceReferences())
        {
            for (Message oldMessage : this.oldCatalog)
            {
                for (String oldReference : oldMessage.getSourceReferences())
                {
                    if (reference.equals(oldReference))
                    {
                        messageList.add(oldMessage);
                        break;
                    }
                }
            }
        }

        if (messageList.isEmpty())
        {
            return;
        }

        Message oldMessage = this.getPreviousMessage(message, messageList);

        if (oldMessage.getMsgctxt() != null && !oldMessage.getMsgctxt().equals(message.getMsgctxt()))
        {
            message.setPrevMsgctx(oldMessage.getMsgctxt());
        }
        if (!message.getMsgid().equals(oldMessage.getMsgid()))
        {
            message.setPrevMsgid(oldMessage.getMsgid());
        }
        if (oldMessage.getMsgidPlural() != null && !oldMessage.getMsgidPlural().equals(message.getMsgidPlural()))
        {
            message.setPrevMsgidPlural(oldMessage.getMsgidPlural());
        }
    }

    private Message getPreviousMessage(Message current, List<Message> oldMessages)
    {
        return oldMessages.get(0); // TODO compare current with old messages and return the best one.
    }

    /**
     * {@inheritDoc}
     *
     * @param config config which shall be used to read the catalog
     *
     * @return
     *
     * @throws CatalogFormatException
     */
    @Override
    public MessageStore read(CatalogConfiguration config) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration) config;
        MessageStore messageStore = new MessageStore();

        Catalog catalog = new Catalog(true);
        PoParser poParser = new PoParser(catalog);
        try
        {
            FileInputStream stream = new FileInputStream(catalogConfig.getTemplateFile());
            catalog = poParser.parseCatalog(stream, catalogConfig.getCharset(), true);
            stream.close();
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

        this.logger.info("The " + this.getClass().getSimpleName() + " read " + messageStore.size() + " messages from the old catalog.");

        return messageStore;
    }

    private Message getHeaderMessage(HeaderConfiguration config, Context velocityContext) throws IOException
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

        if (config.getComments() != null)
        {
            for (String comment : config.getComments(config.getCharset(), velocityContext).split("\n"))
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

            if (!this.comparePluralMsgstr(firstMessage.getMsgstrPlural(), secondMessage.getMsgstrPlural()))
            {
                return false;
            }

            if (firstMessage.isPlural() != secondMessage.isPlural() || (firstMessage.isPlural() && !firstMessage.getMsgidPlural().equals(secondMessage.getMsgidPlural())))
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
     * This methods compares two different lists of plural messages and returns whether they are equals
     *
     * @param first  the first list of plural messages
     * @param second the second list of plural messages
     *
     * @return whether the lists has the same content
     */
    private boolean comparePluralMsgstr(List<String> first, List<String> second)
    {
        if (first.size() != second.size())
        {
            return false;
        }

        for (int i = 0; i < first.size(); i++)
        {
            if (!first.get(i).equals(second.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    /**
     * This method compares the header of the old catalog with the header of the new one.
     *
     * @param old                 the old catalog
     * @param newHeader           the new catalog
     * @param headerConfiguration the header configuration
     *
     * @return returns whether the new header of the catalog is the same as the old one
     */
    private boolean compareCatalogHeader(Message old, Message newHeader, HeaderConfiguration headerConfiguration)
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

        return this.compareHeaderFields(old.getMsgstr().split("\n"), headerConfiguration);
    }

    /**
     * This method compares the fields of a header with the fields which are specified within the
     * header configuration
     *
     * @param fields              the fields of the header
     * @param headerConfiguration the header configuration
     *
     * @return whether the fields fits with the configuration
     */
    public boolean compareHeaderFields(String[] fields, HeaderConfiguration headerConfiguration)
    {
        if (fields.length != headerConfiguration.getMetadata().length)
        {
            return false;
        }

        for (int i = 0; i < fields.length; i++)
        {
            String[] parts = fields[i].split(":");
            MetadataEntry entry = headerConfiguration.getMetadata()[i];

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
     * @param old                 the current catalog
     * @param newCatalog          the new catalog
     * @param headerConfiguration the header configuration of the new catalog
     *
     * @return returns whether the new catalog is the same as the old one
     */
    private boolean compareCatalogs(Catalog old, Catalog newCatalog, HeaderConfiguration headerConfiguration)
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

        return this.compareCatalogHeader(old.locateHeader(), newCatalog.locateHeader(), headerConfiguration);
    }

    @Override
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }
}
