package de.cubeisland.maven.plugins.messagecatalog.mojo;

import de.cubeisland.maven.plugins.messagecatalog.format.*;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.parser.*;
import org.apache.maven.plugin.*;

import java.io.*;
import java.util.*;

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
