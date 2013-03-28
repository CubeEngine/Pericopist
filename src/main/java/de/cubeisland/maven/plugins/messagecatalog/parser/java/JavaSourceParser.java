package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import org.apache.maven.plugin.logging.Log;

public class JavaSourceParser implements SourceParser
{
    private final JavaFileFilter fileFilter;
    private final Log log;

    public JavaSourceParser(Map<String, Object> config, Log log)
    {
        this.fileFilter = new JavaFileFilter();
        this.log = log;
    }

    @Override
    public Set<TranslatableMessage> parse(File sourceDirectory)
    {
        List<File> files = Misc.scanFilesRecursive(sourceDirectory, this.fileFilter);

        MessageVisitor visitor = new MessageVisitor();
        CompilationUnit compilationUnit;
        for (File file : files)
        {
            try
            {
                compilationUnit = JavaParser.parse(file);
                visitor.visit(compilationUnit, file);
            }
            catch (IOException e)
            {}
            catch (ParseException e)
            {
                this.log.warn("Failed to parse the file >" + file.getAbsolutePath() + "<", e);
            }
        }

        return visitor.getMessages();
    }

    private class JavaFileFilter implements FileFilter
    {
        @Override
        public boolean accept(File file)
        {
            return file.getAbsolutePath().endsWith(".java");
        }
    }
}
