/*
 * The MIT License
 * Copyright © 2013 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.pericopist.extractor.java.configuration;

import java.io.File;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.velocity.context.Context;
import org.cubeengine.pericopist.PericopistFactory;
import org.cubeengine.pericopist.configuration.MergeableArray;
import org.cubeengine.pericopist.configuration.MergeableArrayMode;
import org.cubeengine.pericopist.exception.ConfigurationException;
import org.cubeengine.pericopist.extractor.AbstractExtractorConfiguration;
import org.cubeengine.pericopist.extractor.MessageExtractor;
import org.cubeengine.pericopist.extractor.java.JavaMessageExtractor;
import org.cubeengine.pericopist.util.Misc;
import spoon.reflect.declaration.CtElement;

/**
 * <p>
 * This configuration is used for parsing the source tree of java projects and extracting its translatable messages.
 * </p>
 *
 * <p>
 * The configuration can be set up with an xml file.
 * </p>
 *
 * Example:
 *
 * <pre>
 * {@code
 * <source language="java" charset="utf-8"> <!-- default charset: charset set as extractor tag attribute -->
 *     <directory>source file path</directory> <!-- default: ./src/main/java -->
 *     <classpath> <!-- default: System.getProperty("java.class.path").split(File.pathSeparator) -->
 *         <entry>entry</entry>
 *         <entry>another_entry</entry>
 *     </classpath>
 *     <translatables> <!-- register ways how to extract messages -->
 *         <method>
 *             ...
 *         </method>
 *         <constructor>
 *             ...
 *         </constructor>
 *         <annotation>
 *             ...
 *         </annotation>
 *     </translatables>
 * </source>
 * }
 * </pre>
 *
 * The translatables tag has to be filled with information about the
 * {@link JavaExpression} subclasses.
 *
 * @see PericopistFactory#getPericopist(String, Charset, int, Context, Logger)
 * @see org.cubeengine.pericopist.extractor.java.JavaMessageExtractor
 */
@SuppressWarnings("unused")
@XmlRootElement(name = "source")
public class JavaExtractorConfiguration extends AbstractExtractorConfiguration
{
    @MergeableArray(MergeableArrayMode.APPEND_BEFORE)
    private JavaExpression[] javaExpressions;
    @MergeableArray
    private String[] classpathEntries;

    /**
     * This method returns the TranslatableExpression instances describing where the messages shall be extracted.
     *
     * @return TranslatableExpression instances
     */
    public JavaExpression[] getJavaExpressions()
    {
        if (this.javaExpressions == null)
        {
            return new JavaExpression[0];
        }
        return this.javaExpressions;
    }

    /**
     * This method sets the TranslatableExpression instances describing where the messages shall be extracted.
     *
     * @param javaExpressions TranslatableExpression instances
     */
    @XmlElementWrapper(name = "translatables")
    @XmlElements({
            @XmlElement(name = "method", type = Method.class),
            @XmlElement(name = "annotation", type = Annotation.class),
            @XmlElement(name = "constructor", type = Constructor.class)
    })
    public void setJavaExpressions(JavaExpression... javaExpressions)
    {
        this.javaExpressions = javaExpressions;
    }

    /**
     * This method returns the classpath entries of the java project
     *
     * @return classpath entries of the project
     */
    public String[] getClasspathEntries()
    {
        if (this.classpathEntries == null)
        {
            // default value
            return System.getProperty(Misc.JAVA_CLASS_PATH).split(File.pathSeparator);
        }
        return this.classpathEntries;
    }

    /**
     * This method sets the classpath entries of the java project
     *
     * @param classpathEntries classpath entries of the project
     */
    @XmlElementWrapper(name = "classpath")
    @XmlElement(name = "entry")
    public void setClasspathEntries(String... classpathEntries)
    {
        this.classpathEntries = classpathEntries;
    }

    /**
     * This method returns a TranslatableExpression instance which describes the specified CtElement.
     *
     * @param clazz   the class of the TranslatableExpression
     * @param element the CtElement instance which shell be described
     * @param <T>     The class of the TranslatableExpression
     *
     * @return TranslatableExpression instance describing the specified CtElement or null if no expression matches the element
     */
    @SuppressWarnings("unchecked")
    public <T> T getTranslatable(Class<T> clazz, CtElement element)
    {
        for (JavaExpression expression : this.getJavaExpressions())
        {
            // checks whether expression is a subclass of the specified class
            if (!clazz.isAssignableFrom(expression.getClass()))
            {
                continue;
            }

            // checks whether the expression describes the element
            if (expression.matches(element))
            {
                return (T) expression;
            }
        }
        return null;
    }

    @Override
    public Class<? extends MessageExtractor> getExtractorClass()
    {
        return JavaMessageExtractor.class;
    }

    @Override
    public void validate() throws ConfigurationException
    {
        if (this.getJavaExpressions().length == 0)
        {
            throw new ConfigurationException("No translatable expression given!");
        }

        for (JavaExpression expression : this.getJavaExpressions())
        {
            expression.validate();
        }
    }
}
