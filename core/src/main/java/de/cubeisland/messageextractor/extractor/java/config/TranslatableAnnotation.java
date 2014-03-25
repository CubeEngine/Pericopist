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

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "annotation")
public class TranslatableAnnotation
{
    @XmlElement(name = "name", required = true)
    private String fqn;

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private Set<String> fields;

    public TranslatableAnnotation()
    {
        this.fields = new HashSet<String>(1);
        this.fields.add("value");
    }

    public String getFullQualifiedName()
    {
        return this.fqn;
    }

    public String getSimpleName()
    {
        int ind = this.fqn.lastIndexOf(".");
        return ind < 0 ? this.fqn : this.fqn.substring(ind + 1);
    }

    public Set<String> getFields()
    {
        return this.fields;
    }

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
        StringBuilder builder = new StringBuilder(this.fqn);
        int fieldAmount = this.getFields().size();
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
}
