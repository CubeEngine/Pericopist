package de.cubeisland.maven.plugins.messagecatalog.parser.java.config;

import org.w3c.dom.Node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.AbstractSourceConfig;

public class JavaSourceConfig extends AbstractSourceConfig
{
    private Set<TranslatableMethod> translatableMethods;
    private Set<TranslatableAnnotation> translatableAnnotations;

    public Collection<TranslatableMethod> getTranslatableMethods()
    {
        return translatableMethods;
    }

    public Collection<TranslatableAnnotation> getTranslatableAnnotations()
    {
        return translatableAnnotations;
    }

    public void parse(Node node)
    {
        System.out.println("parses node: " + node.getNodeName());

        this.translatableMethods = new HashSet<TranslatableMethod>();
        this.translatableAnnotations = new HashSet<TranslatableAnnotation>();
    }

    public String getLanguage()
    {
        return "java";
    }

    public TranslatableMethod getMethod(String name)
    {
        for (TranslatableMethod method : this.translatableMethods)
        {
            if (method.getName().equals(name))
            {
                return method;
            }
        }
        return null;
    }

    public TranslatableAnnotation getAnnotation(String name)
    {
        for (TranslatableAnnotation annotation : this.translatableAnnotations)
        {
            if (annotation.getFullQualifiedName().equals(name))
            {
                return annotation;
            }
        }
        return null;
    }
}
