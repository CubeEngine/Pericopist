package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import org.apache.velocity.context.Context;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;
import de.cubeisland.maven.plugins.messagecatalog.message.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.util.CatalogHeader;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private Message headerMessage;
    private CatalogHeader catalogHeader;

    private Logger logger;

    public void write(CatalogConfiguration config, Context velocityContext, MessageStore messageStore) throws CatalogFormatException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration)config;
        Catalog catalog = new Catalog(true);

        if (this.logger == null)
        {
            this.logger = Logger.getLogger("messagecatalog_catalogformat");
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

        final File template = catalogConfig.getTemplateFile();

        if(template.exists() && catalogConfig.getDeleteOldTemplate() &&!template.delete())
        {
            throw new CatalogFormatException("The old template could not be deleted.");
        }
        if(catalog.size() == 0 && !catalogConfig.getCreateEmptyTemplate())
        {
            this.logger.info("The project does not contain any translatable message. The template was not created.");
            return;
        }

        try
        {
            this.updateHeaderMessage(catalogConfig, velocityContext);
        }
        catch (IOException e)
        {
            throw new CatalogFormatException("The header could not be created.", e);
        }
        catalog.addMessage(this.headerMessage);

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
                throw new CatalogFormatException("Failed to create the directory '" + directory.getAbsolutePath() + "' !");
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

        this.headerMessage = catalog.locateHeader();

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

    private void updateHeaderMessage(GettextCatalogConfiguration config, Context velocityContext) throws IOException
    {
        if (this.headerMessage == null)
        {
            this.headerMessage = HeaderUtil.generateDefaultHeader();
        }
        HeaderFields headerFields = HeaderFields.wrap(this.headerMessage);
        headerFields.updatePOTCreationDate();

        this.headerMessage = headerFields.unwrap();

        if (this.catalogHeader == null)
        {
            if (config.getHeader() == null)
            {
                return;
            }
            this.catalogHeader = new CatalogHeader(config.getHeader(), velocityContext);
        }
        for (String line : this.catalogHeader.getComments())
        {
            this.headerMessage.addComment(line);
        }
    }

    public String getFileExtension()
    {
        return "pot";
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
