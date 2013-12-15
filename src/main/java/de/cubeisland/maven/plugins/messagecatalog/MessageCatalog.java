package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.context.Context;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfig;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceConfig;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;

public class MessageCatalog
{
    private final Logger logger;
    private final Context context;

    private final SourceParser sourceParser;
    private final SourceConfig sourceConfig;
    private final CatalogFormat catalogFormat;
    private final CatalogConfig catalogConfig;

    public MessageCatalog(SourceParser sourceParser, SourceConfig sourceConfig, CatalogFormat catalogFormat, CatalogConfig catalogConfig, Context context, Logger logger)
    {
        this.sourceParser = sourceParser;
        this.sourceConfig = sourceConfig;
        this.catalogFormat = catalogFormat;
        this.catalogConfig = catalogConfig;

        this.context = context;
        this.logger = logger;
    }

    public SourceConfig getSourceConfig()
    {
        return sourceConfig;
    }

    public CatalogConfig getCatalogConfig()
    {
        return catalogConfig;
    }

    private Context getVelocityContext()
    {
        return this.context;
    }

    private Logger getLogger()
    {
        return this.logger;
    }

    public void generateCatalog()
    {
        this.generateCatalog(null);
    }

    private void generateCatalog(final TranslatableMessageManager manager)
    {
        this.createCatalog(this.parseSourceCode(manager));
    }

    public void updateCatalog()
    {
        TranslatableMessageManager manager = this.readCatalog();
        if (manager == null)
        {
            this.logger.severe("Could not read the old catalog.");
            return;
        }
        this.generateCatalog(manager);
    }

    public TranslatableMessageManager readCatalog()
    {
        try
        {
            return this.catalogFormat.read(this.catalogConfig);
        }
        catch (IOException e)
        {
            this.logger.log(Level.SEVERE, "Could not read the existing catalog.", e);
            return null;
        }
    }

    public TranslatableMessageManager parseSourceCode(TranslatableMessageManager manager)
    {
        return this.sourceParser.parse(this.sourceConfig, manager);
    }

    public void createCatalog(TranslatableMessageManager manager)
    {
        try
        {
            this.catalogFormat.write(this.catalogConfig, manager);
        }
        catch (IOException e)
        {
            this.logger.log(Level.SEVERE, "Could not create the catalog.", e);
        }
    }
}
