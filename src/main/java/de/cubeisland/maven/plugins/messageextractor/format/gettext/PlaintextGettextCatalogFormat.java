package de.cubeisland.maven.plugins.messageextractor.format.gettext;

import org.apache.velocity.context.Context;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messageextractor.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messageextractor.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messageextractor.format.CatalogFormat;
import de.cubeisland.maven.plugins.messageextractor.message.MessageStore;
import de.cubeisland.maven.plugins.messageextractor.message.Occurrence;
import de.cubeisland.maven.plugins.messageextractor.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messageextractor.util.CatalogHeader;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private Catalog oldCatalog;
    private CatalogHeader catalogHeader;

    private Logger logger;

    public void write(CatalogConfiguration config, Context velocityContext, MessageStore messageStore) throws CatalogFormatException
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

        try
        {
            catalog.addMessage(this.getHeaderMessage(catalogConfig, velocityContext));
        }
        catch (IOException e)
        {
            throw new CatalogFormatException("The header could not be created.", e);
        }

        if (this.compareCatalogs(this.oldCatalog, catalog))
        {
            this.logger.info("Did not create a new catalog, because it's the same like the old one.");
            return;
        }
        final File template = catalogConfig.getTemplateFile();

        if (template.exists() && !template.delete())
        {
            throw new CatalogFormatException("The old template could not be deleted.");
        }
        if (catalog.size() == 1 && !catalogConfig.getCreateEmptyTemplate())
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
                poWriter.write(catalog, template);
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

    public MessageStore read(CatalogConfiguration config) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration)config;
        MessageStore messageStore = new MessageStore();

        Catalog catalog = new Catalog(true);
        PoParser poParser = new PoParser(catalog);
        try
        {
            catalog = poParser.parseCatalog(catalogConfig.getTemplateFile());
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

    private Message getHeaderMessage(GettextCatalogConfiguration config, Context velocityContext) throws IOException
    {
        Message headerMessage = null;

        if (this.oldCatalog != null)
        {
            headerMessage = this.oldCatalog.locateHeader();
        }
        if (headerMessage == null)
        {
            headerMessage = HeaderUtil.generateDefaultHeader();
        }
        HeaderFields headerFields = HeaderFields.wrap(headerMessage);
        headerFields.updatePOTCreationDate();

        headerMessage = headerFields.unwrap();

        if (this.catalogHeader == null)
        {
            if (config.getHeader() == null)
            {
                return headerMessage;
            }
            this.catalogHeader = new CatalogHeader(config.getHeader(), velocityContext);
        }
        for (String line : this.catalogHeader.getComments())
        {
            headerMessage.addComment(line);
        }

        return headerMessage;
    }

    private boolean compareCatalogs(Catalog first, Catalog second)
    {
        if (first == null || second == null)
        {
            return false;
        }

        if (first.size() != second.size())
        {
            return false;
        }

        for (Message firstMessage : first)
        {
            if (firstMessage.isHeader())
            {
                continue;
            }

            Message secondMessage = second.locateMessage(firstMessage.getMsgctxt(), firstMessage.getMsgid());

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

    public Logger getLogger()
    {
        return this.logger;
    }

    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }
}
