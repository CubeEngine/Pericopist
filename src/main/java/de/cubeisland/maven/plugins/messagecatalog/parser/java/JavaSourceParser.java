package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import de.cubeisland.maven.plugins.messagecatalog.parser.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

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
        Map<String, TranslatableMessage> messages = new HashMap<String, TranslatableMessage>();
        for (File file : files)
        {
            try
            {
                compilationUnit = JavaParser.parse(file);
                visitor = new SourceClassVisitor(this.basePackage);
                visitor.visit(compilationUnit, file);

                this.mergeMessages(messages, visitor.getMessages());
            }
            catch (IOException ignored)
            {}
            catch (ParseException e)
            {
                this.log.warn("Failed to parse the file >" + file.getAbsolutePath() + "<", e);
            }
        }

        return new TreeSet<TranslatableMessage>(messages.values());
    }

    private void mergeMessages(Map<String, TranslatableMessage> messages, Map<String, TranslatableMessage> fileMessages)
    {
        for(Entry<String, TranslatableMessage> messageEntry : fileMessages.entrySet())
        {
            TranslatableMessage message = messages.get(messageEntry.getKey());
            if(message == null)
            {
                messages.put(messageEntry.getKey(), messageEntry.getValue());
            }
            else
            {
                for(Occurrence occurrence : messageEntry.getValue().getOccurrences())
                {
                    message.addOccurrence(occurrence);
                }
            }
        }
    }

    private class JavaFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getAbsolutePath().endsWith(".java");
        }
    }
}
