/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Phillip Schichtel, Stefan Wolf
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
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
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
import spoon.Launcher;
import spoon.compiler.SpoonCompiler;
import spoon.processing.ProcessingManager;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;

public class JavaMessageExtractor implements MessageExtractor
{
    private Logger logger;
    private ConverterManager converterManager;

    public JavaMessageExtractor()
    {
        this.converterManager = new ConverterManager(true);
    }

    /**
     * {@inheritDoc}
     *
     * @param config the config which shall be used to extract the messages
     *
     * @return
     *
     * @throws MessageExtractionException
     */
    @Override
    public MessageStore extract(ExtractorConfiguration config) throws MessageExtractionException
    {
        return this.extract(config, null);
    }

    /**
     * {@inheritDoc}
     *
     * @param config             the config which shall be used to extract the messages
     * @param loadedMessageStore a messagestore containing the messages from the old catalog
     *
     * @return
     *
     * @throws MessageExtractionException
     */
    @Override
    public MessageStore extract(ExtractorConfiguration config, MessageStore loadedMessageStore) throws MessageExtractionException
    {
        JavaExtractorConfiguration extractorConfig = (JavaExtractorConfiguration) config;

        if (!extractorConfig.getDirectory().exists())
        {
            throw new SourceDirectoryNotExistingException();
        }

        MessageStore messageStore = loadedMessageStore;
        if (messageStore == null)
        {
            messageStore = new MessageStore();
        }
        int messageAmount = messageStore.size();

        try
        {
            Launcher launcher = new Launcher();
            SpoonCompiler compiler = launcher.createCompiler();

            compiler.addInputSource(extractorConfig.getDirectory());
            compiler.setSourceClasspath(extractorConfig.getClasspath());
            this.loadClassLoader(extractorConfig.getClasspath());

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
     * This method creates a new ClassLoader instance which
     * contains the specified classpath and the current one.
     *
     * @param classpath the new classpath
     *
     * @throws MalformedURLException
     */
    private void loadClassLoader(String classpath) throws MalformedURLException
    {
        Set<URL> urls = new HashSet<URL>();
        for (String element : classpath.split(File.pathSeparator))
        {
            urls.add(new File(element).toURI().toURL());
        }

        ClassLoader contextClassLoader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }
}
