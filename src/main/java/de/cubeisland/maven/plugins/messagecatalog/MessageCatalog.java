package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.context.Context;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;

public class MessageCatalog
{
    private final Logger logger;
    private final Context context;

    private final SourceParser sourceParser;
    private final SourceConfiguration sourceConfiguration;
    private final CatalogFormat catalogFormat;
    private final CatalogConfiguration catalogConfiguration;

    public MessageCatalog(SourceParser sourceParser, SourceConfiguration sourceConfiguration, CatalogFormat catalogFormat, CatalogConfiguration catalogConfiguration, Context context, Logger logger)
    {
        this.sourceParser = sourceParser;
        this.sourceConfiguration = sourceConfiguration;
        this.catalogFormat = catalogFormat;
        this.catalogConfiguration = catalogConfiguration;

        this.context = context;
        this.logger = logger;
    }

    public SourceConfiguration getSourceConfiguration()
    {
        return sourceConfiguration;
    }

    public CatalogConfiguration getCatalogConfiguration()
    {
        return catalogConfiguration;
    }

    public Context getVelocityContext()
    {
        return this.context;
    }

    private Logger getLogger()
    {
        return this.logger;
    }

    public void generateCatalog()
    {
        this.createCatalog(this.parseSourceCode());
    }

    private void generateCatalog(final MessageStore manager)
    {
        this.createCatalog(this.parseSourceCode(manager));
    }

    public void updateCatalog()
    {
        MessageStore manager = this.readCatalog();
        if (manager == null)
        {
            this.logger.severe("Could not read the old catalog.");
            return;
        }
        this.generateCatalog(manager);
    }

    public MessageStore readCatalog()
    {
        try
        {
            return this.catalogFormat.read(this, this.catalogConfiguration);
        }
        catch (IOException e)
        {
            this.logger.log(Level.SEVERE, "Could not read the existing catalog.", e);
            return null;
        }
    }

    public MessageStore parseSourceCode()
    {
        return this.sourceParser.parse(this, this.sourceConfiguration);
    }

    public MessageStore parseSourceCode(MessageStore manager)
    {
        return this.sourceParser.parse(this, this.sourceConfiguration, manager);
    }

    public void createCatalog(MessageStore manager)
    {
        try
        {
            this.catalogFormat.write(this, this.catalogConfiguration, manager);
        }
        catch (IOException e)
        {
            this.logger.log(Level.SEVERE, "Could not create the catalog.", e);
        }
    }
}
