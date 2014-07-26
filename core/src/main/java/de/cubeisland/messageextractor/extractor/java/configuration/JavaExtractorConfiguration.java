/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Phillip Schichtel, Stefan Wolf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.cubeisland.messageextractor.extractor.java.configuration;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.extractor.AbstractExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.MessageExtractor;
import de.cubeisland.messageextractor.extractor.java.JavaMessageExtractor;
import de.cubeisland.messageextractor.extractor.java.configuration.Annotation;
import de.cubeisland.messageextractor.extractor.java.configuration.Method;

@XmlRootElement(name = "source")
public class JavaExtractorConfiguration extends AbstractExtractorConfiguration
{
    private Set<Method> methods = new HashSet<Method>();
    private Set<Annotation> annotations = new HashSet<Annotation>();

    public Set<Method> getMethods()
    {
        return methods;
    }

    @XmlElementWrapper(name = "methods")
    @XmlElement(name = "method")
    public void setMethods(Set<Method> methods)
    {
        this.methods = methods;
    }

    public Set<Annotation> getAnnotations()
    {
        return annotations;
    }

    @XmlElementWrapper(name = "annotations")
    @XmlElement(name = "annotation")
    public void setAnnotations(Set<Annotation> annotations)
    {
        this.annotations = annotations;
    }

    public Method getMethod(String name, boolean isStatic)
    {
        System.out.println("Method: " + name);
        for (Method method : this.getMethods())
        {
            if (method.getName().equals(name) && isStatic == method.isStatic())
            {
                return method;
            }
        }
        return null;
    }

    public Annotation getAnnotation(String name)
    {
        for (Annotation annotation : this.getAnnotations())
        {
            if (annotation.getName().equals(name))
            {
                return annotation;
            }
        }
        return null;
    }

    @Override
    public Class<? extends MessageExtractor> getExtractorClass()
    {
        return JavaMessageExtractor.class;
    }

    @Override
    public void validateConfiguration() throws ConfigurationException
    {
        if (this.getAnnotations().size() == 0 && this.getMethods().size() == 0)
        {
            throw new ConfigurationException("You must specify at least one way which describes how to extract the messages");
        }
    }
}
