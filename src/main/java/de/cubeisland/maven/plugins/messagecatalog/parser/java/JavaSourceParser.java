package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cubeisland.maven.plugins.messagecatalog.MessageCatalog;
import de.cubeisland.maven.plugins.messagecatalog.message.MessageStore;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.config.JavaSourceConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class JavaSourceParser implements SourceParser
{
    private final FileFilter fileFilter;
    private Logger logger;

    public JavaSourceParser(Logger logger)
    {
        this.fileFilter = new JavaFileFilter();
        this.logger = logger;
    }

    public MessageStore parse(MessageCatalog messageCatalog, SourceConfiguration config)
    {
        return this.parse(messageCatalog, config, null);
    }

    public MessageStore parse(MessageCatalog messageCatalog, SourceConfiguration config, MessageStore manager)
    {
        JavaSourceConfiguration sourceConfig = (JavaSourceConfiguration)config;

        List<File> files = Misc.scanFilesRecursive(sourceConfig.getDirectory(), this.fileFilter);

        if (manager == null)
        {
            manager = new MessageStore();
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
                SourceClassVisitor visitor = new SourceClassVisitor(sourceConfig, manager, compilationUnit, file);
                compilationUnit.accept(visitor);
            }
            catch (IOException ignored)
            {}
            catch (Exception e)
            {
                this.logger.log(Level.WARNING, "Failed to parse the file >" + file.getAbsolutePath() + "<", e);
            }
        }

        return manager;
    }

    public Class<? extends SourceConfiguration> getConfigClass()
    {
        return JavaSourceConfiguration.class;
    }

    private class JavaFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getAbsolutePath().endsWith(".java");
        }
    }
}
