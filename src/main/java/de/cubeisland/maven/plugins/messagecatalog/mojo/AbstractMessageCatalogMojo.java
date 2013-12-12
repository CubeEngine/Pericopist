package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.context.Context;

import java.io.File;
import java.util.Map.Entry;
import java.util.Properties;

import de.cubeisland.maven.plugins.messagecatalog.config.CatalogConfig;
import de.cubeisland.maven.plugins.messagecatalog.config.Config;
import de.cubeisland.maven.plugins.messagecatalog.config.SourceConfig;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

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
    private SourceConfig source = new SourceConfig();

    /**
     * @parameter
     */
    private CatalogConfig catalog = new CatalogConfig();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        if (this.project != null)
        {
            Context context = this.catalog.getVelocityContext();

            context.put("project", this.project.getModel());
            context.put("artifactId", this.project.getArtifactId());
            context.put("groupId", this.project.getGroupId());
            context.put("version", this.project.getVersion());
            Properties properties = this.project.getProperties();
            for (Entry entry : properties.entrySet())
            {
                context.put((String)entry.getKey(), entry.getValue());
            }
        }

        File header = this.catalog.getHeader();
        if (header != null)
        {
            if(this.project != null)
            {
                header = Misc.getRelativizedFile(this.project.getBasedir(), header);
            }
            else
            {
                header = Misc.getRelativizedFile(new File("."), header);
            }
        }
        this.catalog.setHeader(header);

        this.doExecute(new Config(this.source, this.catalog));
    }

    protected abstract void doExecute(Config config) throws MojoExecutionException, MojoFailureException;
}
