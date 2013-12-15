package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.util.CatalogHeader;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private Message headerMessage;
    private CatalogHeader catalogHeader;
    private Logger logger;

    public void write(MessageCatalog messageCatalog, CatalogConfiguration config, TranslatableMessageManager messageManager) throws IOException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration) config;
        Catalog catalog = new Catalog(true);

        for (TranslatableMessage translatableMessage : messageManager)
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

        this.updateHeaderMessage(messageCatalog, catalogConfig);
        catalog.addMessage(this.headerMessage);

        PoWriter poWriter = new PoWriter(true);
        poWriter.write(catalog, catalogConfig.getTemplateFile());
    }

    public TranslatableMessageManager read(MessageCatalog messageCatalog, CatalogConfiguration config) throws IOException
    {
        GettextCatalogConfiguration catalogConfig = (GettextCatalogConfiguration) config;
        TranslatableMessageManager manager = new TranslatableMessageManager();

        Catalog catalog = new Catalog(true);
        PoParser poParser = new PoParser(catalog);
        catalog = poParser.parseCatalog(catalogConfig.getTemplateFile());

        this.headerMessage = catalog.locateHeader();

        int i = 0;
        for (Message message : catalog)
        {
            if (!message.isHeader())
            {
                manager.addMessage(message.getMsgid(), message.getMsgidPlural(), i++);
            }
        }

        return manager;
    }

    public Class<? extends CatalogConfiguration> getConfigClass()
    {
        return GettextCatalogConfiguration.class;
    }

    private void updateHeaderMessage(MessageCatalog messageCatalog, GettextCatalogConfiguration config)
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
            try
            {
                this.catalogHeader = new CatalogHeader(config.getHeader(), messageCatalog.getVelocityContext());
            }
            catch (FileNotFoundException e)
            {
                this.logger.warning(e.getClass().getName() + ": " + e.getMessage());
                return;
            }
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

    public void init(Logger logger)
    {
        this.logger = logger;
    }
}
