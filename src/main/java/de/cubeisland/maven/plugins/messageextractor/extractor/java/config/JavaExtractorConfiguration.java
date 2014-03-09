package de.cubeisland.maven.plugins.messageextractor.extractor.java.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.maven.plugins.messageextractor.extractor.AbstractExtractorConfiguration;

@XmlRootElement(name = "source")
public class JavaExtractorConfiguration extends AbstractExtractorConfiguration
{
    @XmlElementWrapper(name = "methods")
    @XmlElement(name = "method")
    private Set<TranslatableMethod> translatableMethods = new HashSet<TranslatableMethod>();

    @XmlElementWrapper(name = "annotations")
    @XmlElement(name = "annotation")
    private Set<TranslatableAnnotation> translatableAnnotations = new HashSet<TranslatableAnnotation>();

    public Collection<TranslatableMethod> getTranslatableMethods()
    {
        return translatableMethods;
    }

    public Collection<TranslatableAnnotation> getTranslatableAnnotations()
    {
        return translatableAnnotations;
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
