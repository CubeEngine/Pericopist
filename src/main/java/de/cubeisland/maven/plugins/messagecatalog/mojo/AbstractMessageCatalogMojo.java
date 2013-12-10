package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.*;

import java.io.*;
import java.util.*;

import de.cubeisland.maven.plugins.messagecatalog.util.OptionValues;

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
    public Map<String, Object> options = Collections.emptyMap();

    public void inflateOptions()
    {
        this.options.put(OptionValues.SOURCE_LANGUAGE, this.language);
        this.options.put(OptionValues.OUTPUT_FORMAT, this.outputFormat);
        this.options.put(OptionValues.SOURCE_PATH, this.sourcePath);
        this.options.put(OptionValues.TEMPLATE_FILE, this.templateFile);
        this.options.put(OptionValues.REMOVE_UNUSED_MESSAGES, this.removeUnusedMessages);
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {
        this.inflateOptions();
        this.doExecute();
    }

    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;
}
