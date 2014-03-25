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
package de.cubeisland.messageextractor.extractor.java.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.messageextractor.extractor.AbstractExtractorConfiguration;

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
