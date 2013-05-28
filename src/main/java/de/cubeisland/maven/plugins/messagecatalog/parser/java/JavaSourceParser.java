package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaSourceParser implements SourceParser
{
    private final String basePackage;
    private final FileFilter fileFilter;
    private final Log log;

    public JavaSourceParser(Map<String, Object> config, Log log)
    {
        this.fileFilter = new JavaFileFilter();
        this.log = log;
        if (config.containsKey("basePackage"))
        {
            this.basePackage = String.valueOf(config.get("basePackage"));
        }
        else
        {
            this.basePackage = "de.cubeisland.cubeengine";
        }
    }

    public Set<TranslatableMessage> parse(File sourceDirectory)
    {
        List<File> files = Misc.scanFilesRecursive(sourceDirectory, this.fileFilter);

        SourceClassVisitor visitor;
        CompilationUnit compilationUnit;
        Set<TranslatableMessage> messages = new HashSet<TranslatableMessage>();
        for (File file : files)
        {
            try
            {
                compilationUnit = JavaParser.parse(file);
                visitor = new SourceClassVisitor(this.basePackage);
                visitor.visit(compilationUnit, file);
                messages.addAll(visitor.getMessages());
            }
            catch (IOException ignored)
            {}
            catch (ParseException e)
            {
                this.log.warn("Failed to parse the file >" + file.getAbsolutePath() + "<", e);
            }
        }

        return messages;
    }

    private class JavaFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getAbsolutePath().endsWith(".java");
        }
    }
}
