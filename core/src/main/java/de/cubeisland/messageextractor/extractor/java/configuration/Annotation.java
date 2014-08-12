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
import javax.xml.bind.annotation.XmlRootElement;

import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

/**
 * The annotation describes an annotation.
 * It can be specified with the xml used for the JavaExtractorConfiguration.
 * <p/>
 * Example: <br/>
 * &lt;annotation&gt; <br/>
 * &nbsp;&nbsp;&lt;name&gt;i.am.the.annotation&lt;/name&gt; <br/>
 * &nbsp;&nbsp;&lt;fields&gt; <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;field&gt;translatable_field&lt;/field&gt; <br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;field&gt;second_translatable_field&lt;/field&gt; <br/>
 * &nbsp;&nbsp;&lt;/fields&gt; <br/>
 * &nbsp;&nbsp;&lt;description&gt;I am a default context&lt;/description&gt; <br/>
 * &lt;/annotation&gt;
 */
@XmlRootElement(name = "annotation")
public class Annotation extends TranslatableExpression
{
    private String[] fields;

    /**
     * The constructor adds the value field as the default one
     */
    public Annotation()
    {
        this.fields = new String[] {"value"};
    }

    /**
     * This method returns the translatable fields of the annotation
     *
     * @return translatable fields
     */
    public String[] getFields()
    {
        return this.fields;
    }

    /**
     * This method sets the translatable fields of the annotation
     *
     * @param fields translatable fields
     */
    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    public void setFields(String[] fields)
    {
        this.fields = fields;
    }

    /**
     * This method checks whether the specified string is a translatable field
     *
     * @param name name of a field
     *
     * @return whether the specified name is a translatable field
     */
    public boolean hasField(String name)
    {
        for (String field : this.fields)
        {
            if (field.equals(name))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(this.getName());
        int fieldAmount = this.getFields().length;
        if (fieldAmount != 0)
        {
            builder.append(":");
            for (String field : this.getFields())
            {
                builder.append(field);
                fieldAmount--;
                if (fieldAmount != 0)
                {
                    builder.append(",");
                }
            }
        }
        return builder.toString();
    }

    @Override
    public boolean matches(CtElement element)
    {
        if (!(element instanceof CtAnnotation<?>))
        {
            return false;
        }

        String qfn = ((CtAnnotation<?>) element).getAnnotationType().getQualifiedName();
        return this.getName().equals(qfn);
    }

    @Override
    public boolean hasPlural()
    {
        return false;
    }
}
