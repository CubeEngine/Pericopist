package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

import java.util.Map.Entry;
import java.util.Properties;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.MessageCatalogFactory;
import de.cubeisland.maven.plugins.messagecatalog.exception.MessageCatalogException;

public abstract class AbstractMessageCatalogMojo extends AbstractMojo
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
    private String configuration;

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if(this.project.getPackaging().equalsIgnoreCase("pom"))
        {
            this.getLog().info("Skipped the project '" + this.project.getName() + "' ...");
            return;
        }

        ToolManager toolManager = new ToolManager();
        Context context = toolManager.createContext();

        if (this.project != null)
        {
            context.put("project", this.project.getModel());
            context.put("artifactId", this.project.getArtifactId());
            context.put("groupId", this.project.getGroupId());
            context.put("version", this.project.getVersion());
            context.put("basedir", this.project.getBasedir());
            Properties properties = this.project.getProperties();
            for (Entry entry : properties.entrySet())
            {
                context.put((String)entry.getKey(), entry.getValue());
            }
        }

        try
        {
            MessageCatalogFactory factory = new MessageCatalogFactory();
            this.doExecute(factory.getMessageCatalog(this.configuration, context));
        }
        catch (MessageCatalogException e)
        {
            throw new MojoFailureException(e.getMessage(), e.getCause());
        }
    }

    protected abstract void doExecute(MessageCatalog catalog) throws MessageCatalogException;
}
