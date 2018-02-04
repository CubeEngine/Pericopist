/*
 * pericopist-maven-plugin - A maven plugin to extract messages from your source code and generate message catalogs.
 * Copyright © 2013 Cube Island (development@cubeisland.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cubeengine.maven.plugins.pericopist.mojo;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugins.annotations.Parameter;
import org.cubeengine.pericopist.util.Misc;
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

import org.cubeengine.pericopist.Pericopist;
import org.cubeengine.pericopist.PericopistFactory;
import org.cubeengine.pericopist.exception.ConfigurationException;
import org.cubeengine.pericopist.exception.ConfigurationNotFoundException;
import org.cubeengine.pericopist.exception.PericopistException;
import org.cubeengine.pericopist.exception.SourceDirectoryNotExistingException;

@SuppressWarnings("JavaDoc")
public abstract class AbstractPericopistMojo extends AbstractMojo
{
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project = null;

    @Parameter
    private String[] configurations = null;

    @Parameter
    @SuppressWarnings("FieldCanBeLocal")
    private int readTimeout = 5000;

    @Parameter(defaultValue = "${project.build.sourceEncoding}", readonly = true)
    private String charsetName = null;

    private String classpath = System.getProperty(Misc.JAVA_CLASS_PATH);

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
            throw new MojoFailureException("A pericopist configuration is not specified.");
        }

        // load the classpath of the maven project
        this.loadClasspath();

        Pericopist catalog = this.getPericopist(new PericopistFactory());
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
        catch (PericopistException e)
        {
            throw new MojoFailureException(e.getMessage(), e);
        }

        // revert the classpath
        System.setProperty(Misc.JAVA_CLASS_PATH, this.classpath);
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
     * This method creates and prepares the {@link Pericopist} instance which shall be used to create the catalog
     *
     * @param factory the {@link PericopistFactory} which shall be taken to create the {@link Pericopist} instance
     *
     * @return the {@link Pericopist} instance
     *
     * @throws MojoFailureException
     */
    private Pericopist getPericopist(PericopistFactory factory) throws MojoFailureException, MojoExecutionException
    {
        Context velocityContext = this.getVelocityContext();
        Charset charset = this.getCharset();

        for (String configuration : this.configurations)
        {
            this.getLog().info("uses pericopist configuration '" + configuration + "'.");

            try
            {
                return factory.getPericopist(configuration, charset, this.readTimeout, velocityContext, null);
            }
            catch (ConfigurationNotFoundException e)
            {
                this.getLog().info("Configuration not found: " + configuration);
            }
            catch (ConfigurationException e)
            {
                throw new MojoFailureException("Failed to read the configuration '" + configuration + "'!", e);
            }
            catch (PericopistException e)
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
            Set<Object> elements = new HashSet<>();
            elements.addAll(this.project.getCompileClasspathElements());
            elements.addAll(this.project.getRuntimeClasspathElements());
            elements.addAll(this.project.getSystemClasspathElements());
            elements.addAll(this.project.getTestClasspathElements());

            StringBuilder classpath = new StringBuilder(this.classpath);
            for (Object element : elements)
            {
                classpath.append(File.pathSeparator);
                classpath.append(element.toString());
            }

            System.setProperty(Misc.JAVA_CLASS_PATH, classpath.toString());
        }
        catch (DependencyResolutionRequiredException e)
        {
            throw new MojoFailureException("The dependencies of the maven project couldn't be loaded.", e);
        }
    }

    /**
     * This method is called after a Pericopist instance was loaded successfully by the execute method.
     *
     * @param catalog the {@link Pericopist} instance
     *
     * @throws PericopistException an exception thrown by the {@link Pericopist} instance
     */
    protected abstract void doExecute(Pericopist catalog) throws PericopistException;
}
