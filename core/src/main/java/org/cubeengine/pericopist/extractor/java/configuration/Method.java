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
package org.cubeengine.pericopist.extractor.java.configuration;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * The Method class describes a method invocation.
 * It can be specified with the xml used for the JavaExtractorConfiguration.
 * <p/>
 * Example:
 * <p/>
 * <pre>
 * {@code
 * <method static="false"> <!-- default static: false -->
 *     <name>I.am.the.class#methodname</name>
 *     <signature>
 *         <type>int</type>
 *         <type use-as="singular">java.lang.String</type>
 *         <type use-as="plural">java.lang.String</type>
 *         <type>java.lang.Object[]</type>
 *     </signature>
 *     <description>I am a default context</description>
 * </method>
 * }
 * </pre>
 *
 * @see org.cubeengine.pericopist.extractor.java.configuration.JavaExtractorConfiguration
 */
@SuppressWarnings("unused")
@XmlRootElement(name = "method")
public class Method extends CallableExpression
{
    /**
     * divider which is used between the class and the method name
     */
    public static final String CLASS_METHOD_NAME_DIVIDER = "#";

    private boolean isStatic;

    /**
     * This method returns the information whether it's a static method
     *
     * @return if it's a static method
     */
    public boolean isStatic()
    {
        return this.isStatic;
    }

    /**
     * This method sets whether it's a static method
     *
     * @param isStatic is static
     */
    @XmlAttribute(name = "static")
    public void setStatic(boolean isStatic)
    {
        this.isStatic = isStatic;
    }

    /**
     * This method returns the method name of the method.
     *
     * @return method name
     */
    public String getMethodName()
    {
        String name = this.getName();
        return name.substring(name.lastIndexOf(CLASS_METHOD_NAME_DIVIDER) + 1);
    }

    /**
     * This method returns the class name of the method
     *
     * @return class name
     */
    public String getClassName()
    {
        String name = this.getName();
        return name.substring(0, name.lastIndexOf(CLASS_METHOD_NAME_DIVIDER));
    }

    @Override
    public boolean matches(CtElement element)
    {
        if (!(element instanceof CtInvocation<?>))
        {
            return false;
        }
        CtExecutableReference<?> executable = ((CtInvocation<?>) element).getExecutable();
        if(executable == null)
        {
            return false;
        }

        if (!executable.getSimpleName().equals(this.getMethodName()))
        {
            return false;
        }

        if (this.isStatic() != executable.isStatic())
        {
            return false;
        }
        if (!this.matchesSignature(executable))
        {
            return false;
        }

        return this.isAssignableFrom(executable);
    }

    /**
     * This method checks whether the class of the occurred method invocation is a subclass of the specified one.
     *
     * @param executable method executable
     *
     * @return whether the class of the occurred method is a subclass of the specified one
     */
    private boolean isAssignableFrom(CtExecutableReference<?> executable)
    {
        String className = this.getClassName();

        Queue<CtTypeReference<?>> queue = new ArrayDeque<>();
        queue.offer(executable.getDeclaringType());

        while (!queue.isEmpty())
        {
            CtTypeReference<?> typeReference = queue.poll();

            if (className.equals(typeReference.getQualifiedName()))
            {
                queue.clear();
                return true;
            }

            CtTypeReference<?> superClass = typeReference.getSuperclass();
            Set<CtTypeReference<?>> superInterfaces = typeReference.getSuperInterfaces();

            if (superClass != null)
            {
                queue.offer(typeReference.getSuperclass());
            }
            if (superInterfaces != null)
            {
                for (CtTypeReference<?> interfaceReference : typeReference.getSuperInterfaces())
                {
                    queue.offer(interfaceReference);
                }
            }
        }
        return false;
    }
}
