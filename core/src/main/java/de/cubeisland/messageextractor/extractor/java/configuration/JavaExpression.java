/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.message.AbstractTranslatableExpression;
import spoon.reflect.declaration.CtElement;

/**
 * The TranslatableExpression classes describe where the messages shall be extracted.
 */
public abstract class JavaExpression extends AbstractTranslatableExpression
{
    private String defaultContext;

    /**
     * This method returns the default context which shall be added to every message extracted because of this expression
     *
     * @return default context
     */
    public String getDefaultContext()
    {
        return defaultContext;
    }

    /**
     * This method sets a default context to every message which was extracted because of this expression
     *
     * @param defaultContext default context
     */
    @XmlElement(name = "context")
    public void setDefaultContext(String defaultContext)
    {
        this.defaultContext = defaultContext;
    }

    /**
     * This method validates the translatable expression and checks whether it was
     * set up in the right way
     *
     * @throws ConfigurationException if the name is null
     */
    public void validate() throws ConfigurationException
    {
        if (this.getName() == null)
        {
            throw new ConfigurationException("A translatable expressions needs a name. Specify it with a name tag.");
        }
    }

    /**
     * This method checks whether this translatable expression describes the specified element.
     *
     * @param element element which occured in the MessageProcessor
     *
     * @return if this expression matches the element
     *
     * @see de.cubeisland.messageextractor.extractor.java.processor.MessageProcessor
     */
    public abstract boolean matches(CtElement element);

    /**
     * This method checks whether the translatable expression has a plural
     *
     * @return if a plural exists
     */
    public abstract boolean hasPlural();
}
