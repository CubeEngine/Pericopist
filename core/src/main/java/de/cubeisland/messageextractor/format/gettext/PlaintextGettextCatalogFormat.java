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
package de.cubeisland.messageextractor.format.gettext;

import org.fedorahosted.tennera.jgettext.Catalog;
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

    @Override
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    @Override
    public boolean write(CatalogConfiguration config, OutputStream outputStream, MessageStore messageStore) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration) config;

        GettextHeader header = new GettextHeader(catalogConfig);

        if (!this.hasChanges(messageStore, header))
        {
            this.logger.info("Did not create a new catalog, because it's the same like the old one.");
            return false;
        }

        Catalog catalog = this.getCatalog(catalogConfig, messageStore);
        catalog.addMessage(header.toMessage());
        int messageCount = catalog.size();

        if (messageCount == 1 && !catalogConfig.getCreateEmptyTemplate())
        {
            this.logger.info("The project does not contain any translatable message. The template was not created.");
            return false;
        }

        this.writeCatalog(catalog, catalogConfig, outputStream);
        this.logger.info("The " + this.getClass().getSimpleName() + " created a new template with " + messageCount + " messages (including the header).");
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
    private Catalog getCatalog(GettextCatalogConfiguration configuration, MessageStore messageStore) throws CatalogFormatException
    {
        Catalog catalog = new Catalog(true);

        for (TranslatableMessage translatableMessage : messageStore)
        {
            Message message = this.createMessage(configuration, messageStore, translatableMessage);
            if (message == null)
            {
                continue;
            }

            Message availableMessage = catalog.locateMessage(message.getMsgctxt(), message.getMsgid());
            if (availableMessage == null) // it's a completely new entry
            {
                catalog.addMessage(message);
                continue;
            }

            if (availableMessage.getSourceReferences().isEmpty()) // the old entry isn't used anymore
            {
                catalog.addMessage(message);
                continue;
            }

            throw new CatalogFormatException(String.format("The message with the context '%s' and the msgid '%s' exists already.", message.getMsgctxt(), message.getMsgid()));
        }

        return catalog;
    }

    private Message createMessage(GettextCatalogConfiguration configuration, MessageStore messageStore, TranslatableMessage translatableMessage)
    {
        if (translatableMessage instanceof GettextHeader)
        {
            return null;
        }

        if (translatableMessage instanceof TranslatableGettextMessage)
        {
            Message message = ((TranslatableGettextMessage) translatableMessage).toMessage();

            if (message.getSourceReferences().isEmpty())
            {
                if (configuration.getRemoveUnusedMessages())
                {
                    return null;
                }
                message.setObsolete(true);
                this.logger.info("message with msgid '" + translatableMessage.getSingular() + "' does not occur!");
            }

            return message;
        }

        Message message = new Message();

        // add entries of the message
        message.setMsgctxt(translatableMessage.getContext());
        message.setMsgid(translatableMessage.getSingular());
        if (translatableMessage.hasPlural())
        {
            message.setMsgidPlural(translatableMessage.getPlural());
        }

        for (SourceReference sourceReference : translatableMessage.getSourceReferences())
        {
            message.addSourceReference(sourceReference.getPath(), sourceReference.getLine());
        }
        for (String extractedComment : GettextUtils.createExtractedComments(translatableMessage))
        {
            message.addExtractedComment(extractedComment);
        }

        this.loadEntriesFromPreviousMessage(message, messageStore);

        // fill msgstr plural entries up
        if (message.isPlural())
        {
            for (int i = message.getMsgstrPlural().size(); i < configuration.getPluralAmount(); i++)
            {
                message.addMsgstrPlural("", i);
            }
        }

        return message;
    }

    /**
     * This method sets the previous message ids of the specified message
     *
     * @param message message
     */
    private void loadEntriesFromPreviousMessage(Message message, MessageStore messageStore)
    {
        // adds every message to the list which has the same reference
        List<TranslatableGettextMessage> messageList = new ArrayList<>(1);

        for (String reference : message.getSourceReferences())
        {
            messageLoop: for (TranslatableMessage oldMessage : messageStore)
            {
                if (!(oldMessage instanceof TranslatableGettextMessage))
                {
                    continue; // delete every message which wasn't in the old catalog
                }

                TranslatableGettextMessage translatableGettextMessage = (TranslatableGettextMessage) oldMessage;
                for (SourceReference newReference : translatableGettextMessage.getSourceReferences())
                {
                    // continue with the next message if the reference still exists in that message
                    if (reference.equals(newReference.toString()))
                    {
                        continue messageLoop;
                    }
                }

                for (String oldReference : translatableGettextMessage.getGettextReferences())
                {
                    // if the reference was a message in the old catalog, add it to the message list
                    if (reference.equals(oldReference))
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
        TranslatableGettextMessage oldMessage = this.selectPreviousMessage(message, messageList);

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
        if (message.isPlural() && !message.getMsgidPlural().equals(oldMessage.getPlural()))
        {
            if (oldMessage.hasPlural())
            {
                message.setPrevMsgidPlural(oldMessage.getPlural());
            }
            else
            {
                message.setPrevMsgidPlural(""); // the old message wasn't a plural message
            }
        }
        else
        {
            message.setPrevMsgidPlural(oldMessage.getPrevMsgidPlural());
        }

        message.setDomain(oldMessage.getDomain());
        // sets the msgstr entries
        if (message.isPlural())
        {
            for (int i = 0; i < oldMessage.getMsgstrPlural().size(); i++)
            {
                message.addMsgstrPlural(oldMessage.getMsgstrPlural().get(i), i);
            }
        }
        else if (!oldMessage.hasPlural())
        {
            message.setMsgstr(oldMessage.getMsgstr());
        }
        // sets the translator comments to the new message
        for (String comment : oldMessage.getComments())
        {
            message.addComment(comment);
        }
        // sets the formats (flags) to the new message
        for (String format : oldMessage.getFormats())
        {
            message.addFormat(format);
        }
        message.setAllowWrap(oldMessage.getAllowWrap());
        // set fuzzy true because one should check the entries because it's not sure whether it really was the old message
        message.setFuzzy(true);
    }

    /**
     * This method selects a potential previous message from the specified list
     *
     * @param current     the current message
     * @param oldMessages every message which has a same reference
     *
     * @return a previous message of the current message
     */
    private TranslatableGettextMessage selectPreviousMessage(Message current, List<TranslatableGettextMessage> oldMessages)
    {
        boolean hasPlural = current.isPlural();

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
        boolean hasContext = current.getMsgctxt() != null;
        for (TranslatableGettextMessage message : oldMessages)
        {
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

    private boolean hasChanges(MessageStore messageStore, GettextHeader header)
    {
        GettextHeader oldHeader = null;
        for (TranslatableMessage message : messageStore)
        {
            if (message instanceof GettextHeader)
            {
                oldHeader = (GettextHeader) message;
                continue;
            }
            if (!(message instanceof TranslatableGettextMessage))
            {
                return true;
            }

            if (((TranslatableGettextMessage) message).hasChanges())
            {
                return true;
            }
        }

        return hasChanges(oldHeader, header);
    }

    private boolean hasChanges(GettextHeader oldHeader, GettextHeader newHeader)
    {
        if (oldHeader == null)
        {
            return true;
        }

        // compare header comments
        String[] newComments = newHeader.getComments().toArray(new String[oldHeader.getComments().size()]);
        String[] oldComments = oldHeader.getComments().toArray(new String[oldHeader.getComments().size()]);
        if (oldComments.length != newComments.length)
        {
            return true;
        }

        for (int i = 0; i < oldComments.length; i++)
        {
            if (!oldComments[i].equals(newComments[i]))
            {
                return true;
            }
        }

        // compare header fields
        if (oldHeader.getEntrySize() != newHeader.getEntrySize())
        {
            return true;
        }

        for (int i = 0; i < oldHeader.getEntrySize(); i++)
        {
            MetadataEntry oldEntry = oldHeader.getEntry(i);
            MetadataEntry newEntry = newHeader.getEntry(i);

            if (!oldEntry.equals(newEntry))
            {
                return true;
            }

            if (newEntry.isVariable())
            {
                continue;
            }

            if (!oldEntry.getValue().equals(newEntry.getValue()))
            {
                return true;
            }
        }
        return false;
    }
}
