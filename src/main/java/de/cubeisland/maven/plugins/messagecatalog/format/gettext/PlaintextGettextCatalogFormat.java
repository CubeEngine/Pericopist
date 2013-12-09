package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import org.apache.maven.plugin.logging.Log;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.PoWriter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private final Map<String, Object> config;
    private final Log log;

    private final File base;

    public PlaintextGettextCatalogFormat(Map<String, Object> config, Log log)
    {
        this.config = config;
        this.log = log;

        this.base = (File) config.get("SourcePath");    // TODO modify the way how to get the base!
    }

    public void write(File file, Set<TranslatableMessage> messages) throws IOException
    {
        Catalog catalog = new Catalog(true);

        for(TranslatableMessage translatableMessage : messages)
        {
            Message message = new Message();
            for(Occurrence occurrence : translatableMessage.getOccurrences())
            {
                message.addSourceReference(Misc.getNormalizedRelativePath(this.base, occurrence.getFile()), occurrence.getLine());
            }
            message.setMsgid(translatableMessage.getSingular());
            if(translatableMessage.hasPlural())
            {
                message.setMsgidPlural(translatableMessage.getPlural());
            }

            catalog.addMessage(message);
        }

        catalog.addMessage(this.getHeader(catalog.locateHeader()));

        PoWriter poWriter = new PoWriter(true);
        poWriter.write(catalog, file);
    }

    private Message getHeader(Message existing)
    {
        if(existing != null && existing.isHeader())
        {
            HeaderFields header = HeaderFields.wrap(existing);
            header.updatePOTCreationDate();
            header.updatePOTCreationDate();
            return header.unwrap();
        }
        else
        {
            return HeaderUtil.generateDefaultHeader();
        }
    }

    public String getFileExtension()
    {
        return "pot";
    }
}
