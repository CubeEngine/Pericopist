package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;

import de.cubeisland.maven.plugins.messagecatalog.config.Config;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormatFactory;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParserFactory;

/**
 * Blabla
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMessageCatalogMojo
{
    @Override
    public void doExecute(Config config) throws MojoExecutionException, MojoFailureException
    {
        SourceParser parser = SourceParserFactory.newSourceParser(config.getSource().getLanguage(), config, this.getLog());
        TranslatableMessageManager messageManager = parser.parse(config.getSource().getDirectory(), null);

        CatalogFormat catalogFormat = CatalogFormatFactory.newCatalogFormat(config.getCatalog().getOutputFormat(), config, this.getLog());
        File file = new File(config.getCatalog().getTemplateFile() + "." + catalogFormat.getFileExtension());
        try
        {
            catalogFormat.write(file, messageManager);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Failed to write the message catalog.", e);
        }
    }
}
