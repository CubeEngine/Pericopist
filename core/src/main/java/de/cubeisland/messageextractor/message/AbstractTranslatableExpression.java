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
package de.cubeisland.messageextractor.message;

import javax.xml.bind.annotation.XmlElement;

/**
 * This class is an abstract implementation of {@link de.cubeisland.messageextractor.message.TranslatableExpression}.
 * It implements basic methods which should be used by every {@link de.cubeisland.messageextractor.message.TranslatableExpression} implementation
 */
public abstract class AbstractTranslatableExpression implements TranslatableExpression
{
    private String name;
    private String description;

    /**
     * This method returns the name of the translatable expression
     *
     * @return name
     */
    public String getName()
    {
        return this.name;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    /**
     * This method sets the name of the translatable expression
     *
     * @param name name
     */
    @XmlElement(name = "name")
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * This method sets a description of the translatable expression.
     *
     * @param description a description
     */
    @XmlElement(name = "description")
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getFQN()
    {
        return this.toString();
    }
}
