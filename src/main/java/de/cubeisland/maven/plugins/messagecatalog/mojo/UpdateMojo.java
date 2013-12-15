package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;

/**
 * @goal update
 */
public class UpdateMojo extends AbstractMessageCatalogMojo
{
    @Override
    protected void doExecute(MessageCatalog catalog) throws MojoExecutionException, MojoFailureException
    {
        catalog.updateCatalog();
    }
}
