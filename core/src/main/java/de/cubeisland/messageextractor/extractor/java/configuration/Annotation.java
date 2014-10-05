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
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import de.cubeisland.messageextractor.util.CtAnnotatedElementTypeAdapter;
import spoon.reflect.declaration.CtAnnotatedElementType;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;

/**
 * The annotation describes an annotation.
 * It can be specified with the xml used for the JavaExtractorConfiguration.
 * <p/>
 * Example:
 * <p/>
 * <pre>
 * {@code
 * <annotation>
 *     <name>I.am.the.annotation</name>
 *     <fields> <!-- default field: value -->
 *         <field>translatable_field</field>
 *         <field>second_translatable_field</field>
 *     </fields>
 *     <description>I am a default context</description>
 *     <targets>
 *         <target>method</target> <!-- defines target element types of the annotation -->
 *         <target>type</target>
 *     </targets>
 * </annotation>
 * }
 * </pre>
 * <p/>
 * target element types:
 * <ul>
 * <li>type</li>
 * <li>field</li>
 * <li>method</li>
 * <li>parameter</li>
 * <li>constructor</li>
 * <li>local_variable</li>
 * <li>annotation_type</li>
 * <li>package</li>
 * <li>type_parameter (not supported by spoon yet)</li>
 * <li>type_use (not supported by spoon yet)</li>
 * </ul>
 */
@XmlRootElement(name = "annotation")
public class Annotation extends JavaExpression
{
    private String contextField;
    private String[] fields;

    private CtAnnotatedElementType[] targets;

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
    public void setFields(String... fields)
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

    /**
     * This method returns the context field name of the annotation
     *
     * @return context field name
     */
    public String getContextField()
    {
        return contextField;
    }

    /**
     * This method sets the context field name of the annotation
     *
     * @param contextField context field name
     */
    @XmlElement(name = "contextField")
    public void setContextField(String contextField)
    {
        this.contextField = contextField;
    }

    /**
     * This method returns the targets of the annotation. A target is related to {@link java.lang.annotation.ElementType}
     * and specifies the annotated element type.
     *
     * @return target of the annotation
     */
    public CtAnnotatedElementType[] getTargets()
    {
        return targets;
    }

    /**
     * This method sets the targets of the annotation.
     *
     * @param targets targets of the annotation
     *
     * @see #getTargets()
     */
    @XmlElementWrapper(name = "targets")
    @XmlElement(name = "target")
    @XmlJavaTypeAdapter(CtAnnotatedElementTypeAdapter.class)
    public void setTargets(CtAnnotatedElementType[] targets)
    {
        this.targets = targets;
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

        CtAnnotation<?> annotation = (CtAnnotation<?>) element;

        if (!this.getName().equals(annotation.getAnnotationType().getQualifiedName()))
        {
            return false;
        }

        return this.getTargets() == null || this.containsTarget(annotation.getAnnotatedElementType());
    }

    /**
     * This method checks whether the occurred annotated element type is supported by this annotation.
     *
     * @param type occurred {@link CtAnnotatedElementType}
     *
     * @return whether specified type is supported by this configuration
     */
    private boolean containsTarget(CtAnnotatedElementType type)
    {
        if (type == null)
        {
            return false;
        }

        for (CtAnnotatedElementType elementType : this.getTargets())
        {
            if (type.equals(elementType))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean hasPlural()
    {
        return false;
    }
}
