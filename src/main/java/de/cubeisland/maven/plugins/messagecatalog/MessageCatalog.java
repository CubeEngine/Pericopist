package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.context.Context;

import de.cubeisland.maven.plugins.messagecatalog.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageCatalogException;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageExtractorException;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;
import de.cubeisland.maven.plugins.messagecatalog.parser.MessageExtractor;
import de.cubeisland.maven.plugins.messagecatalog.parser.ExtractorConfiguration;

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
        MessageStore manager = this.readCatalog();
        if (manager == null)
        {
            throw new CatalogFormatException("The old catalog file could not be read.");
        }
        this.generateCatalog(manager);
    }

    private MessageStore readCatalog() throws CatalogFormatException
    {
        return this.catalogFormat.read(this, this.catalogConfiguration);
    }

    private MessageStore parseSourceCode() throws MessageExtractorException
    {
        return this.messageExtractor.parse(this, this.extractorConfiguration);
    }

    private MessageStore parseSourceCode(MessageStore manager) throws MessageExtractorException
    {
        return this.messageExtractor.parse(this, this.extractorConfiguration, manager);
    }

    private void createCatalog(MessageStore messageStore) throws CatalogFormatException
    {
        this.catalogFormat.write(this, this.catalogConfiguration, messageStore);
    }
}
