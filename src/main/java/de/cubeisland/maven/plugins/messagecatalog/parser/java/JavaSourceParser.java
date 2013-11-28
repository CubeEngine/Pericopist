package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import org.apache.maven.plugin.logging.Log;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class JavaSourceParser implements SourceParser
{
    private final FileFilter fileFilter;
    private final Log log;

    private String[] methodNames;
    private Map<String, String[]> annotationFields;
    private String basePackage;

    public JavaSourceParser(Map<String, Object> config, Log log)
    {
        this.fileFilter = new JavaFileFilter();
        this.log = log;
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
                SourceClassVisitor visitor = new SourceClassVisitor(this, compilationUnit, file);
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

    public void setTranslatableMethodNames(String[] methods)
    {
        this.methodNames = methods;
    }

    public boolean isTranslatableMethodName(String name)
    {
        return this.methodNames != null && Arrays.binarySearch(this.methodNames, name) != -1;
    }

    public void setTranslatableAnnotations(Map<String, String[]> annotationFields)
    {
        this.annotationFields = annotationFields;
    }

    public boolean isTranslatableAnnotation(String annotation)
    {
        return this.annotationFields != null && this.annotationFields.get(annotation) != null;
    }

    public boolean isTranslatableAnnotationField(String annotation, String field)
    {
        if (this.annotationFields != null)
        {
            String[] fields = this.annotationFields.get(annotation);
            if(fields != null)
            {
                return Arrays.binarySearch(fields, field) != -1;
            }
        }
        return false;
    }

    public void setBasePackage(String basePackage)
    {
        this.basePackage = basePackage;
    }

    public boolean startsWithBasePackage(String fqn)
    {
        return fqn.startsWith(this.basePackage);
    }

    private class JavaFileFilter implements FileFilter
    {
        public boolean accept(File file)
        {
            return file.getAbsolutePath().endsWith(".java");
        }
    }
}
