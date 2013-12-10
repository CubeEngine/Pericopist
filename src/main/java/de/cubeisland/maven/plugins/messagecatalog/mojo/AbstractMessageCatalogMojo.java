package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.*;

import java.io.*;
import java.util.*;

import de.cubeisland.maven.plugins.messagecatalog.util.Config;

public abstract class AbstractMessageCatalogMojo extends AbstractMojo
{
    public Config config;

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

    private void inflateConfig()
    {
        this.config = new Config();
        this.config.sourcePath = this.sourcePath;
        this.config.templateFile = this.templateFile;
        this.config.language = this.language;
        this.config.outputFormat = this.outputFormat;
        this.config.removeUnusedMessages = this.removeUnusedMessages;

        this.config.options = this.options;
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        this.inflateConfig();
        this.doExecute();
    }

    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;
}
