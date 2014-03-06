package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.context.Context;

import de.cubeisland.maven.plugins.messagecatalog.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageCatalogException;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageExtractorException;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;
import de.cubeisland.maven.plugins.messagecatalog.parser.ExtractorConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.parser.MessageExtractor;

public class MessageCatalog
{
    private final Context context;

    private final MessageExtractor messageExtractor;
    private final ExtractorConfiguration extractorConfiguration;
    private final CatalogFormat catalogFormat;
    private final CatalogConfiguration catalogConfiguration;

    protected MessageCatalog(MessageExtractor messageExtractor, ExtractorConfiguration extractorConfiguration, CatalogFormat catalogFormat, CatalogConfiguration catalogConfiguration, Context context)
    {
        this.messageExtractor = messageExtractor;
        this.extractorConfiguration = extractorConfiguration;
        this.catalogFormat = catalogFormat;
        this.catalogConfiguration = catalogConfiguration;

        this.context = context;
    }

    public ExtractorConfiguration getExtractorConfiguration()
    {
        return this.extractorConfiguration;
    }

    public MessageExtractor getMessageExtractor()
    {
        return this.messageExtractor;
    }

    public CatalogConfiguration getCatalogConfiguration()
    {
        return this.catalogConfiguration;
    }

    public CatalogFormat getCatalogFormat()
    {
        return this.catalogFormat;
    }

    public Context getVelocityContext()
    {
        return this.context;
    }

    public void generateCatalog() throws MessageCatalogException
    {
        this.createCatalog(this.parseSourceCode());
    }

    private void generateCatalog(final MessageStore messageStore) throws MessageCatalogException
    {
        this.createCatalog(this.parseSourceCode(messageStore));
    }

    public void updateCatalog() throws MessageCatalogException
    {
        MessageStore messageStore = null;
        if(this.catalogConfiguration.getTemplateFile().exists())
        {
            messageStore = this.readCatalog();
        }
        this.generateCatalog(messageStore);
    }

    private MessageStore readCatalog() throws CatalogFormatException
    {
        return this.catalogFormat.read(this.catalogConfiguration);
    }

    private MessageStore parseSourceCode() throws MessageExtractorException
    {
        return this.messageExtractor.extract(this.extractorConfiguration);
    }

    private MessageStore parseSourceCode(MessageStore messageStore) throws MessageExtractorException
    {
        return this.messageExtractor.extract(this.extractorConfiguration, messageStore);
    }

    private void createCatalog(MessageStore messageStore) throws CatalogFormatException
    {
        this.catalogFormat.write(this.catalogConfiguration, this.getVelocityContext(), messageStore);
    }
}
