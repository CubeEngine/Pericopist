/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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
package de.cubeisland.messageextractor.extractor.java;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cubeisland.messageextractor.exception.MessageExtractionException;
import de.cubeisland.messageextractor.exception.SourceDirectoryNotExistingException;
import de.cubeisland.messageextractor.extractor.ExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.MessageExtractor;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.java.converter.ConverterManager;
import de.cubeisland.messageextractor.extractor.java.processor.AnnotationProcessor;
import de.cubeisland.messageextractor.extractor.java.processor.CallableExpressionProcessor;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.util.Misc;
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.processing.ProcessingManager;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

public class JavaMessageExtractor implements MessageExtractor
{
    private Logger logger;
    private ConverterManager converterManager;

    private FileFilter javaFileFilter = new FileFilter()
    {
        @Override
        public boolean accept(File pathname)
        {
            return pathname.getName().endsWith(".java");
        }
    };

    public JavaMessageExtractor()
    {
        this.converterManager = new ConverterManager(true);
    }

    @Override
    public MessageStore extract(ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractionException
    {
        JavaExtractorConfiguration extractorConfig = (JavaExtractorConfiguration) config;

        if (!extractorConfig.getDirectory().exists())
        {
            throw new SourceDirectoryNotExistingException();
        }

        int messageAmount = messageStore.size();

        try
        {
            Launcher launcher = new Launcher();
            SpoonCompiler compiler = launcher.createCompiler();
            compiler.addInputSource(extractorConfig.getDirectory());

            String[] classpath = this.loadClasspath(extractorConfig.getClasspathEntries());
            compiler.setSourceClasspath(classpath);
            this.loadClassLoader(classpath);

            compiler.setEncoding(config.getCharset().name());

            Factory spoonFactory = compiler.getFactory();
            ProcessingManager processManager = new QueueProcessingManager(spoonFactory);
            processManager.addProcessor(new CallableExpressionProcessor((JavaExtractorConfiguration) config, messageStore, this.converterManager, this.logger));
            processManager.addProcessor(new AnnotationProcessor((JavaExtractorConfiguration) config, messageStore, this.converterManager, this.logger));

            spoonFactory.getEnvironment().setManager(processManager);
            compiler.build();

            processManager.process();
        }
        catch (FileNotFoundException e)
        {
            throw new MessageExtractionException("A file was not found.", e);
        }
        catch (Exception e)
        {
            throw new MessageExtractionException(e);
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

            if (file.isDirectory())
            {
                List<File> fileList;
                try
                {
                    fileList = Misc.scanFilesRecursive(file, this.javaFileFilter);
                }
                catch (IOException e)
                {
                    this.logger.log(Level.SEVERE, "The classpath entry '" + entry + "' couldn't be scanned for java files. It was removed from the classpath.", e);
                    continue;
                }

                if (fileList.size() > 0)
                {
                    this.logger.warning("The classpath entry '" + entry + "' was removed. The directory contains java files.");
                    continue;
                }
            }

            classpath.add(entry);
        }

        if (classpath.size() == 0)
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
     *
     * @throws MalformedURLException
     */
    private void loadClassLoader(String[] classpath) throws MalformedURLException
    {
        if (classpath == null)
        {
            return;
        }

        Set<URL> urls = new HashSet<>();
        for (String element : classpath)
        {
            urls.add(new File(element).toURI().toURL());
        }

        ClassLoader contextClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
}
