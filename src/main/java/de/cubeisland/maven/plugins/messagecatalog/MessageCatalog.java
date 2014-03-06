package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.context.Context;

import de.cubeisland.maven.plugins.messagecatalog.exception.CatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageCatalogException;
import de.cubeisland.maven.plugins.messagecatalog.exception.SourceParserException;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;

public class MessageCatalog
{
    private final Context context;

    private final SourceParser sourceParser;
    private final SourceConfiguration sourceConfiguration;
    private final CatalogFormat catalogFormat;
    private final CatalogConfiguration catalogConfiguration;

    protected MessageCatalog(SourceParser sourceParser, SourceConfiguration sourceConfiguration, CatalogFormat catalogFormat, CatalogConfiguration catalogConfiguration, Context context)
    {
        this.sourceParser = sourceParser;
        this.sourceConfiguration = sourceConfiguration;
        this.catalogFormat = catalogFormat;
        this.catalogConfiguration = catalogConfiguration;

        this.context = context;
    }

    public SourceConfiguration getSourceConfiguration()
    {
        return this.sourceConfiguration;
    }

    public SourceParser getSourceParser()
    {
        return this.sourceParser;
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

    private MessageStore parseSourceCode() throws SourceParserException
    {
        return this.sourceParser.parse(this, this.sourceConfiguration);
    }

    private MessageStore parseSourceCode(MessageStore manager) throws SourceParserException
    {
        return this.sourceParser.parse(this, this.sourceConfiguration, manager);
    }

    private void createCatalog(MessageStore messageStore) throws CatalogFormatException
    {
        this.catalogFormat.write(this, this.catalogConfiguration, messageStore);
    }
}
