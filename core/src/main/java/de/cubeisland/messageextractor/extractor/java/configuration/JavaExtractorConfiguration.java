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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.extractor.AbstractExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.MessageExtractor;
import de.cubeisland.messageextractor.extractor.java.JavaMessageExtractor;
import spoon.reflect.declaration.CtElement;

@XmlRootElement(name = "source")
public class JavaExtractorConfiguration extends AbstractExtractorConfiguration
{
    private TranslatableExpression[] translatableExpressions;
    private String classpath = System.getProperty("java.class.path");

    public TranslatableExpression[] getTranslatableExpressions()
    {
        return translatableExpressions;
    }

    @XmlElementWrapper(name = "translatables")
    @XmlElements({
            @XmlElement(name = "method", type = Method.class),
            @XmlElement(name = "annotation", type = Annotation.class),
            @XmlElement(name = "constructor", type = Constructor.class)
    })
    public void setTranslatableExpressions(TranslatableExpression[] translatableExpressions)
    {
        this.translatableExpressions = translatableExpressions;
    }

    public String getClasspath()
    {
        return this.classpath;
    }

    @XmlElement(name = "classpath")
    public void setClasspath(String classpath)
    {
        this.classpath = classpath;
    }

    public <T> T getTranslatable(Class<T> clazz, CtElement element)
    {
        for (TranslatableExpression expression : this.getTranslatableExpressions())
        {
            if (!clazz.isAssignableFrom(expression.getClass()))
            {
                continue;
            }

            if (expression.matches(element))
            {
                return (T) expression;
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
        if (this.getTranslatableExpressions().length == 0)
        {
            throw new ConfigurationException("You must specify at least one way which describes how to extract the messages");
        }

        for (TranslatableExpression expression : this.getTranslatableExpressions())
        {
            expression.validate();
        }
    }
}
