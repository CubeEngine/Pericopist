package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import org.apache.maven.plugin.logging.Log;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.cubeisland.maven.plugins.messagecatalog.config.Config;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.util.CatalogHeader;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private final Config config;
    private final Log log;

    private Message headerMessage;
    private CatalogHeader catalogHeader;

    public PlaintextGettextCatalogFormat(Config config, Log log)
    {
        this.config = config;
        this.log = log;
    }

    public void write(File file, TranslatableMessageManager messageManager) throws IOException
    {
        Catalog catalog = new Catalog(true);

        for (TranslatableMessage translatableMessage : messageManager)
        {
            if (translatableMessage.getOccurrences().isEmpty())
            {
                if (this.config.getCatalog().getRemoveUnusedMessages())
                {
                    continue;
                }
                else
                {
                    this.log.info("message with msgid '" + translatableMessage.getSingular() + "' does not occur!");
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

        this.updateHeaderMessage();
        catalog.addMessage(this.headerMessage);

        PoWriter poWriter = new PoWriter(true);
        poWriter.write(catalog, file);
    }

    public TranslatableMessageManager read(File file) throws IOException
    {
        TranslatableMessageManager manager = new TranslatableMessageManager();

        Catalog catalog = new Catalog(true);
        PoParser poParser = new PoParser(catalog);
        catalog = poParser.parseCatalog(file);

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

    private void updateHeaderMessage()
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
            if (this.config.getCatalog().getHeader() == null)
            {
                return;
            }
            try
            {
                this.catalogHeader = new CatalogHeader(this.config.getCatalog().getHeader(), this.config.getCatalog().getVelocityContext());
            }
            catch (FileNotFoundException e)
            {
                this.log.warn(e.getClass().getName() + ": " + e.getMessage());
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
}
