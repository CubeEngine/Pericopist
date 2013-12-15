package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.MessageCatalogFactory;

/**
 * Blabla
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMessageCatalogMojo
{
    @Override
    public void doExecute(MessageCatalog factory) throws MojoExecutionException, MojoFailureException
    {
//        SourceParser parser = SourceParserFactory.newSourceParser(config.getSource().getLanguage(), config, this.getLog());
//        TranslatableMessageManager messageManager = parser.parse(config.getSource().getDirectory(), null);
//
//        CatalogFormat catalogFormat = CatalogFormatFactory.newCatalogFormat(config.getCatalog().getOutputFormat(), config, this.getLog());
//        File file = new File(config.getCatalog().getTemplateFile() + "." + catalogFormat.getFileExtension());
//        try
//        {
//            catalogFormat.write(file, messageManager);
//        }
//        catch (IOException e)
//        {
//            throw new MojoExecutionException("Failed to write the message catalog.", e);
//        }
    }
}
