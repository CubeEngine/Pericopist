package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import de.cubeisland.maven.plugins.messagecatalog.util.Config;

public abstract class AbstractMessageCatalogMojo extends AbstractMojo
{
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
        this.doExecute(new Config(this.language, this.sourcePath, this.templateFile, this.outputFormat, this.removeUnusedMessages, this.options));
    }

    protected abstract void doExecute(Config config) throws MojoExecutionException, MojoFailureException;
}
