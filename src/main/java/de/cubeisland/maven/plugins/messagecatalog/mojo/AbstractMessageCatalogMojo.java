package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
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
    public boolean addDefaultTools = false;

    /**
     * @parameter
     */
    public Map<String, String> options = Collections.emptyMap();

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        ToolManager toolManager = new ToolManager(this.addDefaultTools);

        ToolContext context = toolManager.createContext();
        if (this.project != null)
        {
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

        File header = null;
        if (this.headerFile != null)
        {
            if(this.project != null)
            {
                header = Misc.getRelativizedFile(this.project.getBasedir(), this.headerFile);
            }
            else
            {
                header = Misc.getRelativizedFile(new File(""), this.headerFile);
            }
        }

        this.doExecute(new Config(this.language, this.sourcePath, this.templateFile, this.outputFormat, this.removeUnusedMessages, header, context, this.options));
    }

    protected abstract void doExecute(Config config) throws MojoExecutionException, MojoFailureException;
}
