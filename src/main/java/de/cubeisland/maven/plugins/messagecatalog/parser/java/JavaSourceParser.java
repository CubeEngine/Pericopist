package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;
import org.apache.maven.plugin.logging.Log;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

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

        String[] environment = new String[files.size()];
        for(int i = 0; i < environment.length; i++)
        {
            environment[i] = files.get(i).getAbsolutePath();
        }

        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setEnvironment(null, environment, null, true);
        parser.setCompilerOptions(options);

        Map<String, TranslatableMessage> messages = new HashMap<String, TranslatableMessage>();
        for (File file : files)
        {
            try
            {
                parser.setSource(Misc.parseFileToCharArray(file));
                CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
                SourceClassVisitor visitor = new SourceClassVisitor(compilationUnit, file, this.basePackage);
                compilationUnit.accept(visitor);

                this.mergeMessages(messages, visitor.getMessages());
            }
            catch (IOException ignored)
            {}
            catch (Exception e)
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
