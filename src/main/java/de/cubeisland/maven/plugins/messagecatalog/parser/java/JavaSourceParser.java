package de.cubeisland.maven.plugins.messagecatalog.parser.java;

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

import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables.TranslatableMethod;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class JavaSourceParser implements SourceParser
{
    private final FileFilter fileFilter;
    private final Log log;

    private JavaParserConfiguration configuration;

    public JavaSourceParser(Map<String, Object> config, Log log)
    {
        this.fileFilter = new JavaFileFilter();
        this.log = log;

        Set<TranslatableMethod> methodSet = null;

        String methods = (String) config.get("methods");
        if (methods != null)
        {
            methodSet = new HashSet<TranslatableMethod>();
            for(String method : methods.split(" "))
            {
                try
                {
                    TranslatableMethod translatableMethod = new TranslatableMethod(method);
                    methodSet.add(translatableMethod);
                    this.log.info("translatable method '" + translatableMethod + "' was added");
                }
                catch (Exception e)
                {
                    this.log.error("The translatable method '" + method + "' could not be added", e);
                }
            }
        }

        this.configuration = new JavaParserConfiguration(methodSet);
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

        Set<TranslatableMessage> messages = new HashSet<TranslatableMessage>();
        for (File file : files)
        {
            try
            {
                parser.setSource(Misc.parseFileToCharArray(file));
                CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
                SourceClassVisitor visitor = new SourceClassVisitor(this.configuration, compilationUnit, file);
                compilationUnit.accept(visitor);

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
