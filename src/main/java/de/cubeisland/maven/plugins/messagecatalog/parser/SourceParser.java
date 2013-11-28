package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface SourceParser
{
    public Set<TranslatableMessage> parse(File sourceDirectory);

    public void setTranslatableMethodNames(String[] methods);

    public boolean isTranslatableMethodName(String name);

    public void setTranslatableAnnotations(Map<String, String[]> annotationFields);

    public boolean isTranslatableAnnotation(String annotation);

    public boolean isTranslatableAnnotationField(String annotation, String field);

    public void setBasePackage(String basePackage);

    public boolean startsWithBasePackage(String fqn);
}
