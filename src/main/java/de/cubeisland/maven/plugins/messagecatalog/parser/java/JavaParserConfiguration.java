package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import java.util.HashSet;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables.TranslatableAnnotation;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables.TranslatableMethod;

public class JavaParserConfiguration
{
    private final Set<TranslatableMethod> methods;
    private final Set<TranslatableAnnotation> annotations;

    public JavaParserConfiguration()
    {
        this(null);
    }

    public JavaParserConfiguration(Set<TranslatableMethod> methods)
    {
        this(methods, null);
    }

    public JavaParserConfiguration(Set<TranslatableMethod> methods, Set<TranslatableAnnotation> annotations)
    {
        if (methods != null)
        {
            this.methods = methods;
        }
        else
        {
            this.methods = new HashSet<TranslatableMethod>();
            this.methods.add(new TranslatableMethod("_:0"));
            this.methods.add(new TranslatableMethod("translate:0,1"));
        }
        if (annotations != null)
        {
            this.annotations = annotations;
        }
        else
        {
            this.annotations = new HashSet<TranslatableAnnotation>(1);
        }
    }

    public TranslatableMethod getMethod(String name)
    {
        for(TranslatableMethod method : this.methods)
        {
            if(method.getName().equals(name))
            {
                return method;
            }
        }
        return null;
    }

    public Set<TranslatableMethod> getMethods()
    {
        return this.methods;
    }

    public Set<TranslatableAnnotation> getAnnotations()
    {
        return this.annotations;
    }

    public TranslatableAnnotation getAnnotation(String name)
    {
        for(TranslatableAnnotation annotation : this.annotations)
        {
            if(annotation.getFullQualifiedName().equals(name))
            {
                return annotation;
            }
        }
        return null;
    }
}
