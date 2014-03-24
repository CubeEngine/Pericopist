package de.cubeisland.maven.plugins.messageextractor;

import org.apache.velocity.context.Context;

import java.nio.charset.Charset;

import de.cubeisland.maven.plugins.messageextractor.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messageextractor.exception.MessageCatalogException;
import de.cubeisland.maven.plugins.messageextractor.exception.MessageExtractionException;
import de.cubeisland.maven.plugins.messageextractor.extractor.ExtractorConfiguration;
import de.cubeisland.maven.plugins.messageextractor.extractor.MessageExtractor;
import de.cubeisland.maven.plugins.messageextractor.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messageextractor.format.CatalogFormat;
import de.cubeisland.maven.plugins.messageextractor.message.MessageStore;

public class MessageCatalog
{
    private final Context context;

    private final MessageExtractor messageExtractor;
    private final ExtractorConfiguration extractorConfiguration;
    private final CatalogFormat catalogFormat;
    private final CatalogConfiguration catalogConfiguration;

    private Charset charset;

    protected MessageCatalog(MessageExtractor messageExtractor, ExtractorConfiguration extractorConfiguration, CatalogFormat catalogFormat, CatalogConfiguration catalogConfiguration, Context context)
    {
        this.messageExtractor = messageExtractor;
        this.extractorConfiguration = extractorConfiguration;
        this.catalogFormat = catalogFormat;
        this.catalogConfiguration = catalogConfiguration;

        this.context = context;

        if (this.catalogConfiguration.getCharsetName() == null)
        {
            this.charset = Charset.forName("UTF-8");
        }
        else
        {
            this.charset = Charset.forName(this.catalogConfiguration.getCharsetName());
        }
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

    public Charset getCharset()
    {
        return this.charset;
    }

    public void setCharset(Charset charset)
    {
        this.charset = charset;
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
        if (this.catalogConfiguration.getTemplateFile().exists())
        {
            messageStore = this.readCatalog();
        }
        this.generateCatalog(messageStore);
    }

    private MessageStore readCatalog() throws CatalogFormatException
    {
        return this.catalogFormat.read(this.catalogConfiguration, this.getCharset());
    }

    private MessageStore parseSourceCode() throws MessageExtractionException
    {
        return this.messageExtractor.extract(this.extractorConfiguration);
    }

    private MessageStore parseSourceCode(MessageStore messageStore) throws MessageExtractionException
    {
        return this.messageExtractor.extract(this.extractorConfiguration, messageStore);
    }

    private void createCatalog(MessageStore messageStore) throws CatalogFormatException
    {
        this.catalogFormat.write(this.catalogConfiguration, this.getCharset(), this.getVelocityContext(), messageStore);
    }
}
