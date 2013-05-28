package de.cubeisland.maven.plugins.messagecatalog.mojo;

import org.apache.maven.plugin.*;

import java.io.*;
import java.util.*;

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
    public Map<String, Object> config = Collections.emptyMap();


    public void execute() throws MojoExecutionException, MojoFailureException
    {
        this.doExecute();
    }

    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;
}
