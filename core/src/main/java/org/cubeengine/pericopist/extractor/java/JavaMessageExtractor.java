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
package org.cubeengine.pericopist.extractor.java;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.cubeengine.pericopist.exception.MessageExtractionException;
import org.cubeengine.pericopist.exception.SourceDirectoryNotExistingException;
import org.cubeengine.pericopist.extractor.ExtractorConfiguration;
import org.cubeengine.pericopist.extractor.MessageExtractor;
import org.cubeengine.pericopist.extractor.java.configuration.JavaExtractorConfiguration;
import org.cubeengine.pericopist.extractor.java.converter.ConverterManager;
import org.cubeengine.pericopist.extractor.java.processor.AnnotationProcessor;
import org.cubeengine.pericopist.extractor.java.processor.CallableExpressionProcessor;
import org.cubeengine.pericopist.message.MessageStore;
import spoon.Launcher;
import spoon.SpoonModelBuilder;
import spoon.processing.Processor;
import spoon.reflect.declaration.CtElement;

/**
 * This {@link MessageExtractor} implementation is used for extracting the programming language java.
 */
public class JavaMessageExtractor implements MessageExtractor
{
    private Logger logger;
    private final ConverterManager converterManager;

    public JavaMessageExtractor()
    {
        this.converterManager = new ConverterManager(true);
    }

    @Override
    public MessageStore extract(ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractionException
    {
        JavaExtractorConfiguration extractorConfig = (JavaExtractorConfiguration)config;

        if (!extractorConfig.getDirectory().exists())
        {
            throw new SourceDirectoryNotExistingException();
        }

        int messageAmount = messageStore.size();

        try
        {
            Launcher launcher = new Launcher();
            SpoonModelBuilder compiler = launcher.createCompiler();
            compiler.addInputSource(extractorConfig.getDirectory());

            String[] classpath = this.loadClasspath(extractorConfig.getClasspathEntries());
            compiler.setSourceClasspath(classpath);
            this.loadClassLoader(classpath);

            compiler.setEncoding(config.getCharset().name());
            compiler.build();

            Collection<Processor<? extends CtElement>> processors = Arrays.<Processor<? extends CtElement>>asList(
                new CallableExpressionProcessor((JavaExtractorConfiguration) config, messageStore, this.converterManager, this.logger),
                new AnnotationProcessor((JavaExtractorConfiguration) config, messageStore, this.converterManager, this.logger)
            );

            compiler.process(processors);
        }
        catch (Exception e)
        {
            throw new MessageExtractionException("An error occurred while extracting the messages", e);
        }

        this.logger.info("The " + this.getClass().getSimpleName() + " extracted " + (messageStore.size() - messageAmount) + " new messages from the source code.");

        return messageStore;
    }

    @Override
    public void setLogger(Logger logger)
    {
        this.logger = logger;
    }

    /**
     * This method creates a classpath from the specified classpath entries. It also removes non existing entries
     * and directories which contains java files.
     *
     * @param classpathEntries classpath entries
     *
     * @return the classpath
     */
    private String[] loadClasspath(String[] classpathEntries)
    {
        List<String> classpath = new ArrayList<>(classpathEntries.length);
        for (String entry : classpathEntries)
        {
            File file = new File(entry);

            if (!file.exists())
            {
                this.logger.warning("The classpath entry '" + entry + "' was removed. It doesn't exist.");
                continue;
            }

            classpath.add(entry);
        }

        if (classpath.isEmpty())
        {
            this.logger.warning("The classpath is empty.");
        }

        return classpath.toArray(new String[classpath.size()]);
    }

    /**
     * This method creates a new ClassLoader instance which
     * contains the specified classpath and the current one.
     *
     * @param classpath the new classpath entries
     */
    private void loadClassLoader(String[] classpath) throws MalformedURLException
    {
        if (classpath == null)
        {
            return;
        }

        Set<URI> uris = new HashSet<>();
        for (String element : classpath)
        {
            uris.add(new File(element).toURI());
        }

        URL[] urls = new URL[uris.size()];
        int i = 0;
        for (URI uri : uris)
        {
            urls[i++] = uri.toURL();
        }

        ClassLoader contextClassLoader = URLClassLoader.newInstance(urls, Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
}
