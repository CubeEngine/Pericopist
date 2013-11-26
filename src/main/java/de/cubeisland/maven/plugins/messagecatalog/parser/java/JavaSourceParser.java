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

        SourceClassVisitor visitor;
        CompilationUnit compilationUnit;
        Set<TranslatableMessage> messages = new HashSet<TranslatableMessage>();
        for (File file : files)
        {
            try
            {
                parser.setSource(Misc.parseFileToCharArray(file));
                compilationUnit = (CompilationUnit) parser.createAST(null);
                visitor = new SourceClassVisitor(compilationUnit, this.basePackage);
                compilationUnit.accept(visitor);
//                visitor.visit(compilationUnit, file);
                messages.addAll(visitor.getMessages());
            }
            catch (IOException ignored)
            {}
            catch (Exception e)
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
