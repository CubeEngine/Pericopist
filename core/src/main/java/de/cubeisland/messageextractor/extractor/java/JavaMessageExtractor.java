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

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import de.cubeisland.messageextractor.exception.MessageExtractionException;
import de.cubeisland.messageextractor.exception.SourceDirectoryNotExistingException;
import de.cubeisland.messageextractor.extractor.ExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.MessageExtractor;
import de.cubeisland.messageextractor.extractor.java.config.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.util.Misc;

public class JavaMessageExtractor implements MessageExtractor
{
    private final FileFilter fileFilter;

    public JavaMessageExtractor()
    {
        this.fileFilter = new JavaFileFilter();
    }

    public MessageStore extract(ExtractorConfiguration config, Charset charset) throws MessageExtractionException
    {
        return this.extract(config, charset, null);
    }

    public MessageStore extract(ExtractorConfiguration config, Charset charset, MessageStore loadedMessageStore) throws MessageExtractionException
    {
        JavaExtractorConfiguration extractorConfig = (JavaExtractorConfiguration) config;

        if (!extractorConfig.getDirectory().exists())
        {
            throw new SourceDirectoryNotExistingException();
        }
        List<File> files;
        try
        {
            files = Misc.scanFilesRecursive(extractorConfig.getDirectory(), this.fileFilter);
        }
        catch (IOException e)
        {
            throw new MessageExtractionException("Failed to enlist the applicable files!", e);
        }

        MessageStore messageStore = loadedMessageStore;
        if (messageStore == null)
        {
            messageStore = new MessageStore();
        }

        String[] environment = new String[files.size()];
        for (int i = 0; i < environment.length; i++)
        {
            environment[i] = files.get(i).getAbsolutePath();
        }

        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setEnvironment(null, environment, null, true);
        parser.setCompilerOptions(options);

        for (File file : files)
        {
            try
            {
                parser.setSource(Misc.parseFileToCharArray(file, charset));
                CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
                SourceClassVisitor visitor = new SourceClassVisitor(extractorConfig, messageStore, compilationUnit, file);
                compilationUnit.accept(visitor);
            }
            catch (IOException e)
            {
                throw new MessageExtractionException("The file on path '" + file.getAbsolutePath() + "' could not be parsed.", e);
            }
        }

        return messageStore;
    }

    public Class<? extends ExtractorConfiguration> getConfigClass()
    {
        return JavaExtractorConfiguration.class;
    }
}
