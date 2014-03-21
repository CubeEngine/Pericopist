package de.cubeisland.maven.plugins.messageextractor.extractor.java;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import de.cubeisland.maven.plugins.messageextractor.exception.MessageExtractionException;
import de.cubeisland.maven.plugins.messageextractor.exception.SourceDirectoryNotExistingException;
import de.cubeisland.maven.plugins.messageextractor.extractor.ExtractorConfiguration;
import de.cubeisland.maven.plugins.messageextractor.extractor.MessageExtractor;
import de.cubeisland.maven.plugins.messageextractor.extractor.java.config.JavaExtractorConfiguration;
import de.cubeisland.maven.plugins.messageextractor.message.MessageStore;
import de.cubeisland.maven.plugins.messageextractor.util.Misc;

public class JavaMessageExtractor implements MessageExtractor
{
    private final FileFilter fileFilter;

    public JavaMessageExtractor()
    {
        this.fileFilter = new JavaFileFilter();
    }

    public MessageStore extract(ExtractorConfiguration config) throws MessageExtractionException
    {
        return this.extract(config, null);
    }

    public MessageStore extract(ExtractorConfiguration config, MessageStore messageStore) throws MessageExtractionException
    {
        JavaExtractorConfiguration extractorConfig = (JavaExtractorConfiguration)config;

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
                parser.setSource(Misc.parseFileToCharArray(file));
                CompilationUnit compilationUnit = (CompilationUnit)parser.createAST(null);
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

    private class JavaFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getAbsolutePath().endsWith(".java");
        }
    }
}
