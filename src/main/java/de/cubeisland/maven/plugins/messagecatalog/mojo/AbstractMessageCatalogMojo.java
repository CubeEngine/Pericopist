package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import de.cubeisland.maven.plugins.messagecatalog.util.Config;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public abstract class AbstractMessageCatalogMojo extends AbstractMojo
{
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    public MavenProject project;

    /**
     * @parameter
     */
    public File sourcePath = new File(".");

    /**
     * @parameter
     */
    public String templateFile = "src/main/resources/en_US";

    /**
     * @parameter
     */
    public File headerFile;

    /**
     * @parameter
     */
    public String language = "java";

    /**
     * @parameter
     */
    public String outputFormat = "gettext";

    /**
     * @parameter
     */
    public boolean removeUnusedMessages = true;

    /**
     * @parameter
     */
    public Map<String, String> options = Collections.emptyMap();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        VelocityContext velocityContext = new VelocityContext();
        if (this.project != null)
        {
            velocityContext.put("project", this.project.getModel());
            velocityContext.put("artifactId", this.project.getArtifactId());
            velocityContext.put("groupId", this.project.getGroupId());
            velocityContext.put("version", this.project.getVersion());
            Properties properties = this.project.getProperties();
            for (Entry entry : properties.entrySet())
            {
                velocityContext.put((String)entry.getKey(), entry.getValue());
            }
        }
        velocityContext.put("date", new DateTool());

        File header = null;
        if (this.headerFile != null)
        {
            header = Misc.getRelativizedFile(this.project.getBasedir(), this.headerFile);
        }

        this.doExecute(new Config(this.language, this.sourcePath, this.templateFile, this.outputFormat, this.removeUnusedMessages, header, velocityContext, this.options));
    }

    protected abstract void doExecute(Config config) throws MojoExecutionException, MojoFailureException;
}
