package de.cubeisland.cubeengine.util.gettextgen.parser.java;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JavaParserConfiguration
{
    private final Set<String> methods;
    private final Map<String, Set<String>> annotationFields;

    public JavaParserConfiguration()
    {
        this(null);
    }

    public JavaParserConfiguration(Set<String> methods)
    {
        this(methods, null);
    }

    public JavaParserConfiguration(Set<String> methods, Map<String, Set<String>> annotationFields)
    {
        if (methods != null)
        {
            this.methods = methods;
        }
        else
        {
            this.methods = new HashSet<String>();
        }
        if (annotationFields != null)
        {
            this.annotationFields = annotationFields;
        }
        else
        {
            this.annotationFields = new HashMap<String, Set<String>>();
        }
    }

    public Set<String> getMethods()
    {
        return this.methods;
    }

    public Map<String, Set<String>> getAnnotationFields()
    {
        return this.annotationFields;
    }
}
