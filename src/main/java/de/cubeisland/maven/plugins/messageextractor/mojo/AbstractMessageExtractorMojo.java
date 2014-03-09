package de.cubeisland.maven.plugins.messageextractor.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

import java.util.Map.Entry;
import java.util.Properties;

import de.cubeisland.maven.plugins.messageextractor.MessageCatalog;
import de.cubeisland.maven.plugins.messageextractor.MessageCatalogFactory;
import de.cubeisland.maven.plugins.messageextractor.exception.ConfigurationNotFoundException;
import de.cubeisland.maven.plugins.messageextractor.exception.MessageCatalogException;
import de.cubeisland.maven.plugins.messageextractor.exception.SourceDirectoryNotExistsException;

public abstract class AbstractMessageExtractorMojo extends AbstractMojo
{
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter
     */
    private String[] configurations;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (this.project == null)
        {
            throw new MojoFailureException("It's not a maven project, isn't it?");
        }
        if (this.project.getPackaging().equalsIgnoreCase("pom"))
        {
            this.getLog().info("Skipped the project '" + this.project.getName() + "' ...");
            return;
        }

        if (this.configurations == null || this.configurations.length == 0)
        {
            throw new MojoFailureException("An extractor configuration is not specified.");
        }

        ToolManager toolManager = new ToolManager();
        Context velocityContext = toolManager.createContext();

        velocityContext.put("project", this.project.getModel());
        velocityContext.put("artifactId", this.project.getArtifactId());
        velocityContext.put("groupId", this.project.getGroupId());
        velocityContext.put("version", this.project.getVersion());
        velocityContext.put("basedir", this.project.getBasedir());
        Properties properties = this.project.getProperties();
        for (Entry entry : properties.entrySet())
        {
            velocityContext.put((String)entry.getKey(), entry.getValue());
        }

        MessageCatalogFactory factory = new MessageCatalogFactory();

        boolean foundConfiguration = false;
        int i = 0;
        do
        {
            String configuration = this.configurations[i++];

            this.getLog().info("uses extractor configuration '" + configuration + "'.");

            try
            {
                this.doExecute(factory.getMessageCatalog(configuration, velocityContext));
                foundConfiguration = true;
            }
            catch (ConfigurationNotFoundException e)
            {
                this.getLog().warn("Build the template failed.", e);
            }
            catch (SourceDirectoryNotExistsException e)
            {
                this.getLog().info(e.getMessage());
                this.getLog().info("Skipped the project '" + this.project.getName() + "'' ...", e);
            }
            catch (MessageCatalogException e)
            {
                throw new MojoFailureException(e.getMessage(), e);
            }
        }
        while (!foundConfiguration && i < this.configurations.length);

        if (!foundConfiguration)
        {
            throw new MojoFailureException("The template could not be created. Did not find any of the specified configurations.");
        }
    }

    protected abstract void doExecute(MessageCatalog catalog) throws MessageCatalogException;
}
