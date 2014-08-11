/**
 * This file is part of messageextractor-maven-plugin.
 * messageextractor-maven-plugin is licensed under the GNU General Public License Version 3.
 *
 * messageextractor-maven-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * messageextractor-maven-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with messageextractor-maven-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cubeisland.maven.plugins.messageextractor.mojo;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import de.cubeisland.messageextractor.MessageCatalog;
import de.cubeisland.messageextractor.MessageCatalogFactory;
import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.exception.ConfigurationNotFoundException;
import de.cubeisland.messageextractor.exception.MessageCatalogException;
import de.cubeisland.messageextractor.exception.SourceDirectoryNotExistingException;
import de.cubeisland.messageextractor.util.ResourceLoader;

/**
 * @requiresDependencyResolution test
 * @threadSafe true
 */
public abstract class AbstractMessageExtractorMojo extends AbstractMojo
{
    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    private MavenProject project = null;

    /**
     * @parameter
     */
    private String[] configurations = null;

    /**
     * @parameter default-value="${project.build.sourceEncoding}"
     * @readonly
     */
    private String charsetName = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException
    {
        if (this.project == null)
        {
            throw new MojoFailureException("It's not a maven project, isn't it?");
        }
        if ("pom".equalsIgnoreCase(this.project.getPackaging()))
        {
            this.getLog().info("Skipped the project '" + this.project.getName() + "' ...");
            return;
        }
        if (this.configurations == null || this.configurations.length == 0)
        {
            throw new MojoFailureException("An extractor configuration is not specified.");
        }

        this.loadClasspath();

        MessageCatalog catalog = this.getMessageCatalog(new MessageCatalogFactory());
        if (catalog == null)
        {
            throw new MojoFailureException("The template could not be created. Did not find any of the specified configurations.");
        }

        try
        {
            this.doExecute(catalog);
        }
        catch (SourceDirectoryNotExistingException e)
        {
            this.getLog().info(e.getMessage());
            this.getLog().info("Skipped the project '" + this.project.getName() + "'' ...", e);
        }
        catch (MessageCatalogException e)
        {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    /**
     * This method creates and prepares a velocity context
     *
     * @return the prepared velocity context
     */
    private Context getVelocityContext()
    {
        ToolManager toolManager = new ToolManager();
        Context velocityContext = toolManager.createContext();

        velocityContext.put("project", this.project.getModel());
        velocityContext.put("artifactId", this.project.getArtifactId());
        velocityContext.put("groupId", this.project.getGroupId());
        velocityContext.put("version", this.project.getVersion());
        velocityContext.put("basedir", this.project.getBasedir());
        velocityContext.put("sourceEncoding", this.charsetName);
        velocityContext.put("resource", new ResourceLoader());
        Properties properties = this.project.getProperties();
        for (Entry entry : properties.entrySet())
        {
            velocityContext.put((String) entry.getKey(), entry.getValue());
        }

        return velocityContext;
    }

    /**
     * This method returns the charset which shall be used as default by the message catalog
     *
     * @return the default charset
     */
    private Charset getCharset()
    {
        if (this.charsetName != null)
        {
            return Charset.forName(this.charsetName);
        }
        return Charset.forName("UTF-8");
    }

    /**
     * This method creates and prepares the MessageCatalog instance which shall be used to create the catalog
     *
     * @param factory the MessageCatalogFactory which shall be taken to create the MessageCatalog instance
     *
     * @return the MessageCatalog instance
     *
     * @throws MojoFailureException
     */
    private MessageCatalog getMessageCatalog(MessageCatalogFactory factory) throws MojoFailureException, MojoExecutionException
    {
        Context velocityContext = this.getVelocityContext();
        Charset charset = this.getCharset();

        for (String configuration : this.configurations)
        {
            this.getLog().info("uses extractor configuration '" + configuration + "'.");

            try
            {
                return factory.getMessageCatalog(configuration, charset, velocityContext);
            }
            catch (ConfigurationNotFoundException e)
            {
                this.getLog().info("Configuration not found: " + configuration);
            }
            catch (ConfigurationException e)
            {
                throw new MojoFailureException("Failed to read the configuration '" + configuration + "'!", e);
            }
            catch (MessageCatalogException e)
            {
                throw new MojoExecutionException(e.getMessage(), e);
            }
        }

        return null;
    }

    /**
     * This method loads the classpath of the maven project and adds the classpath elements
     * to the current classpath
     */
    private void loadClasspath() throws MojoFailureException
    {
        try
        {
            Set<Object> elements = new HashSet<Object>();
            elements.addAll(this.project.getCompileClasspathElements());
            elements.addAll(this.project.getRuntimeClasspathElements());
            elements.addAll(this.project.getSystemClasspathElements());
            elements.addAll(this.project.getTestClasspathElements());

            StringBuilder classpath = new StringBuilder(System.getProperty("java.class.path"));
            for (Object element : elements)
            {
                classpath.append(File.pathSeparator);
                classpath.append(element.toString());
            }

            System.setProperty("java.class.path", classpath.toString());
        }
        catch (DependencyResolutionRequiredException e)
        {
            throw new MojoFailureException("The dependencies of the maven project couldn't be loaded.", e);
        }
    }

    /**
     * This method is called after a MessageCatalog instance was loaded successfully by the execute method.
     *
     * @param catalog the MessageCatalog instance
     *
     * @throws MessageCatalogException an exception thrown by the MessageCatalog instance
     */
    protected abstract void doExecute(MessageCatalog catalog) throws MessageCatalogException;
}
