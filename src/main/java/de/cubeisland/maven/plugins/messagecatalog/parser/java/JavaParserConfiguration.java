package de.cubeisland.maven.plugins.messagecatalog.parser.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables.TranslatableMethod;

public class JavaParserConfiguration
{
    private final Set<TranslatableMethod> methods;
//    private final Map<String, Set<String>> annotationFields;

    public JavaParserConfiguration()
    {
        this(null);
    }

    public JavaParserConfiguration(Set<TranslatableMethod> methods)
    {
        this(methods, null);
    }

    public JavaParserConfiguration(Set<TranslatableMethod> methods, Map<String, Set<String>> annotationFields)
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
//        if (annotationFields != null)
//        {
//            this.annotationFields = annotationFields;
//        }
//        else
//        {
//            this.annotationFields = new HashMap<String, Set<String>>();
//        }
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

//    public Map<String, Set<String>> getAnnotationFields()
//    {
//        return this.annotationFields;
//    }
}
