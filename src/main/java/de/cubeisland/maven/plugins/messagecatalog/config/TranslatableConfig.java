package de.cubeisland.maven.plugins.messagecatalog.config;

import java.util.List;

public class TranslatableConfig
{
    private List<String> methods;
    private List<String> annotations;

    public List<String> getMethods()
    {
        return methods;
    }

    public void setMethods(List<String> methods)
    {
        this.methods = methods;
    }

    public List<String> getAnnotations()
    {
        return annotations;
    }

    public void setAnnotations(List<String> annotations)
    {
        this.annotations = annotations;
    }
}
