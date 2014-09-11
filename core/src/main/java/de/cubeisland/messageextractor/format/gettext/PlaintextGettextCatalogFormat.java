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

import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.cubeisland.messageextractor.exception.CatalogFormatException;
import de.cubeisland.messageextractor.format.CatalogConfiguration;
import de.cubeisland.messageextractor.format.CatalogFormat;
import de.cubeisland.messageextractor.format.HeaderConfiguration;
import de.cubeisland.messageextractor.format.HeaderConfiguration.MetadataEntry;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.SourceReference;
import de.cubeisland.messageextractor.message.TranslatableMessage;

/**
 * This catalog format creates and reads gettext catalogs.
 *
 * @see de.cubeisland.messageextractor.format.gettext.GettextCatalogConfiguration
 */
public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private Logger logger;

    /**
     * {@inheritDoc}
     *
     * @param config       config which shall be used to write the catalog
     * @param messageStore the message store containing the messages for the catalog
     *
     * @throws CatalogFormatException
     */
    @Override
    public boolean write(CatalogConfiguration config, OutputStream outputStream, MessageStore messageStore) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration) config;

        if (!this.hasChanges(messageStore, catalogConfig))
        {
            this.logger.info("Did not create a new catalog, because it's the same like the old one.");
            return false;
        }

        Catalog catalog = this.getCatalog(catalogConfig, messageStore);
        int messageCount = catalog.size();

        if (catalogConfig.getHeaderConfiguration() != null)
        {
            try
            {
                catalog.addMessage(this.getHeaderMessage(catalogConfig.getHeaderConfiguration()));
            }
            catch (IOException e)
            {
                throw new CatalogFormatException("The header could not be created.", e);
            }
        }

        if (messageCount == 0 && !catalogConfig.getCreateEmptyTemplate())
        {
            this.logger.info("The project does not contain any translatable message. The template was not created.");
            return false;
        }

        this.writeCatalog(catalog, catalogConfig, outputStream);
        this.logger.info("The " + this.getClass().getSimpleName() + " created a new template with " + messageCount + " messages.");
        return true;
    }

    /**
     * This method writes the catalog file into the specified output stream
     *
     * @param catalog       catalog instance
     * @param configuration configuration of the catalog
     * @param outputStream  output stream of the catalog
     *
     * @throws CatalogFormatException if the catalog couldn't be created
     */
    private void writeCatalog(Catalog catalog, GettextCatalogConfiguration configuration, OutputStream outputStream) throws CatalogFormatException
    {
        final PoWriter poWriter = new PoWriter(true);
        try
        {
            poWriter.write(catalog, outputStream, configuration.getCharset());
        }
        catch (IOException e)
        {
            throw new CatalogFormatException("The catalog could not be created", e);
        }
    }

    /**
     * This method creates a catalog instance
     *
     * @param configuration configuration of the catalog
     * @param messageStore  the message store containing the messages for the catalog
     *
     * @return catalog containing the specified information
     */
    private Catalog getCatalog(GettextCatalogConfiguration configuration, MessageStore messageStore)
    {
        Catalog catalog = new Catalog(true);

        for (TranslatableMessage translatableMessage : messageStore)
        {
            if (translatableMessage instanceof GettextHeader)
            {
                continue;
            }

            if (translatableMessage instanceof TranslatableGettextMessage)
            {
                Message message = ((TranslatableGettextMessage) translatableMessage).toMessage();

                if (message.getSourceReferences().isEmpty())
                {
                    if (configuration.getRemoveUnusedMessages())
                    {
                        continue;
                    }
                    message.setObsolete(true);
                    this.logger.info("message with msgid '" + translatableMessage.getSingular() + "' does not occur!");
                }

                catalog.addMessage(message);
                continue;
            }

            Message message = new Message();

            message.setMsgctxt(translatableMessage.getContext());
            message.setMsgid(translatableMessage.getSingular());
            if (translatableMessage.hasPlural())
            {
                message.setMsgidPlural(translatableMessage.getPlural());
                for (int i = 0; i < configuration.getPluralAmount(); i++)
                {
                    message.addMsgstrPlural("", i);
                }
            }

            this.setPreviousMessageIds(message, messageStore);

            for (SourceReference sourceReference : translatableMessage.getSourceReferences())
            {
                message.addSourceReference(sourceReference.getPath(), sourceReference.getLine());
            }
            for (String extractedComment : translatableMessage.getExtractedComments())
            {
                message.addExtractedComment(extractedComment);
            }

            catalog.addMessage(message);
        }

        return catalog;
    }

    /**
     * This method sets the previous message ids of the specified message
     *
     * @param message message
     */
    private void setPreviousMessageIds(Message message, MessageStore messageStore)
    {
        // adds every message to the list which has the same reference
        List<TranslatableGettextMessage> messageList = new ArrayList<>(1);
        for (String reference : message.getSourceReferences())
        {
            for (TranslatableMessage oldMessage : messageStore)
            {
                if (!(oldMessage instanceof TranslatableGettextMessage))
                {
                    continue; // delete every message which wasn't in the old catalog
                }

                for (SourceReference oldReference : oldMessage.getSourceReferences())
                {
                    if (reference.equals(oldReference.toString())) // TODO is the method right?
                    {
                        messageList.add((TranslatableGettextMessage) oldMessage);
                        break;
                    }
                }
            }
        }

        // return from the method because there is no message with the same reference
        if (messageList.isEmpty())
        {
            return;
        }

        TranslatableGettextMessage oldMessage = this.getPreviousMessage(message, messageList);

        // prev msgctxt
        if (oldMessage.hasContext() && !oldMessage.getContext().equals(message.getMsgctxt()))
        {
            message.setPrevMsgctx(oldMessage.getContext());
        }
        else
        {
            message.setPrevMsgctx(oldMessage.getPrevMsgctx());
        }

        //prev msgid
        if (!oldMessage.getSingular().equals(message.getMsgid()))
        {
            message.setPrevMsgid(oldMessage.getSingular());
        }
        else
        {
            message.setPrevMsgid(oldMessage.getPrevMsgid());
        }

        // prev msgid_plural
        if (oldMessage.hasPlural() && !oldMessage.getPlural().equals(message.getMsgidPlural()))
        {
            message.setPrevMsgidPlural(oldMessage.getPlural());
        }
        else
        {
            message.setPrevMsgidPlural(oldMessage.getPrevMsgidPlural());
        }
    }

    /**
     * This method returns a previous message from the specified list
     *
     * @param current     the current message
     * @param oldMessages every old message which has a same reference
     *
     * @return a previous message of the current message
     */
    private TranslatableGettextMessage getPreviousMessage(Message current, List<TranslatableGettextMessage> oldMessages)
    {
        boolean hasPlural = current.getMsgidPlural() != null;

        // check whether a message has the same msgid
        for (TranslatableGettextMessage message : oldMessages)
        {
            boolean samePluralState = (hasPlural && message.hasPlural()) || (!hasPlural && !message.hasPlural());
            if (current.getMsgid().equals(message.getSingular()) && samePluralState)
            {
                return message;
            }
        }

        // if message is a plural message, check whether a message has the same plural msgid
        if (hasPlural)
        {
            for (TranslatableGettextMessage message : oldMessages)
            {
                if (!message.hasPlural())
                {
                    continue;
                }

                if (current.getMsgidPlural().equals(message.getPlural()))
                {
                    return message;
                }
            }
        }

        // take the first message with the same context
        for (TranslatableGettextMessage message : oldMessages)
        {
            boolean hasContext = current.getMsgctxt() != null;
            if (message.hasContext() && hasContext)
            {
                return message;
            }
            else if (!message.hasContext() && !hasContext)
            {
                return message;
            }
        }

        // take the first message
        return oldMessages.get(0);
    }

    @Override
    public MessageStore read(CatalogConfiguration config, InputStream inputStream) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration) config;
        MessageStore messageStore = new MessageStore();

        Catalog catalog = new Catalog(true);
        PoParser poParser = new PoParser(catalog);
        catalog = poParser.parseCatalog(inputStream, catalogConfig.getCharset(), true);

        Message header = catalog.locateHeader();
        if (header != null)
        {
            messageStore.addMessage(new GettextHeader(header));
        }

        int i = 1;
        for (Message catalogMessage : catalog)
        {
            if (catalogMessage.isHeader())
            {
                continue;
            }
            messageStore.addMessage(new TranslatableGettextMessage(catalogMessage, i++));
        }

        this.logger.info("The " + this.getClass().getSimpleName() + " read " + messageStore.size() + " messages (including the header) from the old catalog.");

        return messageStore;
    }

    /**
     * This method returns the header message which fits the specified parameters.
     *
     * @param config the configuration of the header
     *
     * @return header message
     *
     * @throws IOException if the comments resource couldn't be found
     */
    private Message getHeaderMessage(HeaderConfiguration config) throws IOException
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
            for (String comment : config.getComments().split("\n"))
            {
                headerMessage.addComment(comment);
            }
        }

        return headerMessage;
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
    public boolean compareHeaderFields(String[] fields, HeaderConfiguration headerConfiguration) // TODO is it still needed?
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

    private boolean hasChanges(MessageStore messageStore, GettextCatalogConfiguration catalogConfig)
    {
        GettextHeader header = null;
        for (TranslatableMessage message : messageStore)
        {
            if (message instanceof GettextHeader)
            {
                header = (GettextHeader) message;
                continue;
            }
            if (!(message instanceof TranslatableGettextMessage))
            {
                return true;
            }

            if (this.hasChanges((TranslatableGettextMessage) message))
            {
                return true;
            }
        }

        return this.hasChanges(header, catalogConfig);
    }

    private boolean hasChanges(TranslatableGettextMessage message)
    {
        // 1. compare source references
        if (message.getSourceReferences().size() != message.getGettextReferences().size())
        {
            return true;
        }

        int i = 0;
        for (SourceReference reference : message.getSourceReferences())
        {
            if (!reference.toString().equals(message.getGettextReferences().get(i++)))
            {
                return true;
            }
        }

        // 2. compare extracted comments
        if (message.getExtractedComments().size() != message.getExtractedCommentsFromGettext().size())
        {
            return true;
        }

        i = 0;
        for (String extractedComment : message.getExtractedComments())
        {
            int j = 0;
            for (String extractedCommentFromGettext : message.getExtractedCommentsFromGettext())
            {
                if (j <= i)
                {
                    j++;
                }
                else
                {
                    if (!extractedComment.equals(extractedCommentFromGettext))
                    {
                        return true;
                    }
                    break;
                }
            }
            i++;
        }

        return false;
    }

    private boolean hasChanges(GettextHeader header, GettextCatalogConfiguration configuration) // TODO compare header!
    {
        HeaderConfiguration headerConfiguration = configuration.getHeaderConfiguration();

        if (header == null && headerConfiguration == null)
        {
            return false;
        }
        else if (headerConfiguration == null || header == null)
        {
            return true;
        }

        String[] comments = headerConfiguration.getComments().split("\n");
        if (header.getComments().size() != comments.length)
        {
            return true;
        }
        return false;
        //        String[] firstComments = old.getComments().toArray(new String[old.getComments().size()]);
        //        String[] secondComments = newHeader.getComments().toArray(new String[firstComments.length]);
        //
        //        for (int i = 0; i < firstComments.length; i++)
        //        {
        //            if (!firstComments[i].equals(secondComments[i]))
        //            {
        //                return false;
        //            }
        //        }
        //
        //        return this.compareHeaderFields(old.getMsgstr().split("\n"), headerConfiguration);
    }

    @Override
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }
}
