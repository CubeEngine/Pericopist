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

/**
 * Blabla
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMessageCatalogMojo
{
    @Override
    public void doExecute() throws MojoExecutionException, MojoFailureException
    {
        SourceParser parser = SourceParserFactory.newSourceParser(this.language, this.config, this.getLog());
        TranslatableMessageManager messageManager = parser.parse (this.sourcePath, null);

        CatalogFormat catalogFormat = CatalogFormatFactory.newCatalogFormat(this.outputFormat, this.config, this.getLog());
        File file = new File(this.templateFile + "." + catalogFormat.getFileExtension());
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
