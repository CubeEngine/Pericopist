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

import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceConfig;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.config.JavaSourceConfig;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class JavaSourceParser implements SourceParser
{
    private final FileFilter fileFilter;
    private Logger logger;

    public JavaSourceParser()
    {
        this.fileFilter = new JavaFileFilter();
    }

    public TranslatableMessageManager parse(SourceConfig config, TranslatableMessageManager manager)
    {
        JavaSourceConfig sourceConfig = (JavaSourceConfig) config;

        List<File> files = Misc.scanFilesRecursive(sourceConfig.getDirectory(), this.fileFilter);

        if (manager == null)
        {
            manager = new TranslatableMessageManager();
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

    public Class<? extends SourceConfig> getSourceConfigClass()
    {
        return JavaSourceConfig.class;
    }

    public void init(Logger logger)
    {
        this.logger = logger;
    }

    private class JavaFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getAbsolutePath().endsWith(".java");
        }
    }
}
