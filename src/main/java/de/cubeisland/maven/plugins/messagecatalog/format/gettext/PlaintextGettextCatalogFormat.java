package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import org.apache.maven.plugin.logging.Log;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoParser;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.util.OptionValues;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private final Map<String, Object> config;
    private final Log log;

    private Message headerMessage;

    public PlaintextGettextCatalogFormat(Map<String, Object> config, Log log)
    {
        this.config = config;
        this.log = log;
    }

    public void write(File file, Set<TranslatableMessage> messages) throws IOException
    {
        Catalog catalog = new Catalog(true);

        for(TranslatableMessage translatableMessage : messages)
        {
            if(translatableMessage.getOccurrences().isEmpty() && (Boolean) this.config.get(OptionValues.REMOVE_UNUSED_MESSAGES))
            {
                continue;
            }
            Message message = new Message();
            for(Occurrence occurrence : translatableMessage.getOccurrences())
            {
                message.addSourceReference(occurrence.getPath(), occurrence.getLine());
            }
            message.setMsgid(translatableMessage.getSingular());
            if(translatableMessage.hasPlural())
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
        for(Message message : catalog)
        {
            if(!message.isHeader())
            {
                manager.addMessage(message.getMsgid(), message.getMsgidPlural(), i++);
            }
        }

        return manager;
    }

    private void updateHeaderMessage()
    {
        if (this.headerMessage != null)
        {
            HeaderFields header = HeaderFields.wrap(this.headerMessage);
            header.updatePOTCreationDate();

            Message updatedHeader = header.unwrap();
            for(String comment : this.headerMessage.getComments())
            {
                updatedHeader.addComment(comment);
            }
            this.headerMessage = updatedHeader;
        }
        else
        {
            this.headerMessage = HeaderUtil.generateDefaultHeader();
        }
    }

    public String getFileExtension()
    {
        return "pot";
    }
}
