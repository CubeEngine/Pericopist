package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;

/**
 * Blabla
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMessageCatalogMojo
{
    @Override
    public void doExecute(MessageCatalog catalog) throws MojoExecutionException, MojoFailureException
    {
        catalog.generateCatalog();
    }
}
