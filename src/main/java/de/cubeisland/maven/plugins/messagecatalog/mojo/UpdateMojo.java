package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.IOException;

import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormatFactory;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParserFactory;
import de.cubeisland.maven.plugins.messagecatalog.util.Config;

/**
 * @goal update
 */
public class UpdateMojo extends AbstractMessageCatalogMojo
{
    @Override
    protected void doExecute(Config config) throws MojoExecutionException, MojoFailureException
    {
        CatalogFormat catalogFormat = CatalogFormatFactory.newCatalogFormat(config.getOutputFormat(), config, this.getLog());    // create catalogFormat
        File file = new File(config.getTemplateFile() + "." + catalogFormat.getFileExtension());   // load pot file

        TranslatableMessageManager messageManager;
        try
        {
            messageManager = catalogFormat.read(file);      // try to read existing catalog file
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Failed to read the existing message catalog.", e);
        }

        SourceParser parser = SourceParserFactory.newSourceParser(config.getSourceLanguage(), config, this.getLog());  // create SourceParser
        messageManager = parser.parse(config.getSourcePath(), messageManager);     // search source files for translatable string literals

        try
        {
            catalogFormat.write(file, messageManager);    // write new catalog file
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Failed to write the message catalog.", e);
        }
    }
}
