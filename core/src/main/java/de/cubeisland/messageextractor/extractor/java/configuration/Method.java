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

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

@XmlRootElement(name = "method")
public class Method extends CallableExpression
{
    public static final String CLASS_METHOD_NAME_DIVIDER = "#";

    private boolean isStatic;

    public boolean isStatic()
    {
        return this.isStatic;
    }

    @XmlAttribute(name = "static")
    public void setStatic(boolean isStatic)
    {
        this.isStatic = isStatic;
    }

    public String getMethodName()
    {
        String name = this.getName();
        return name.substring(name.lastIndexOf(CLASS_METHOD_NAME_DIVIDER) + 1);
    }

    public String getClassName()
    {
        String name = this.getName();
        return name.substring(0, name.lastIndexOf(CLASS_METHOD_NAME_DIVIDER));
    }

    @Override
    public String toString()
    {
        return this.getName() + ":" + this.getSingularIndex() + (this.hasPlural() ? "," + this.getPluralIndex() : "");
    }

    @Override
    public boolean matches(CtElement element)
    {
        if (!(element instanceof CtInvocation<?>))
        {
            return false;
        }
        CtExecutableReference<?> executable = ((CtInvocation<?>) element).getExecutable();

        if(!executable.getSimpleName().equals(this.getMethodName()))
        {
            return false;
        }

        if(this.isStatic() != executable.isStatic())
        {
            return false;
        }
        if (!this.matchesSignature(executable))
        {
            return false;
        }

        return this.isAssignableFrom(executable);
    }

    private boolean isAssignableFrom(CtExecutableReference<?> executable)
    {
        String className = this.getClassName();

        Queue<CtTypeReference<?>> queue = new ArrayDeque<CtTypeReference<?>>();
        queue.offer(executable.getDeclaringType());

        while (!queue.isEmpty())
        {
            CtTypeReference<?> typeReference = queue.poll();

            if(className.equals(typeReference.getQualifiedName()))
            {
                queue.clear();
                return true;
            }

            CtTypeReference<?> superClass = typeReference.getSuperclass();
            Set<CtTypeReference<?>> superInterfaces = typeReference.getSuperInterfaces();

            if(superClass != null)
            {
                queue.offer(typeReference.getSuperclass());
            }
            if(superInterfaces != null)
            {
                for(CtTypeReference<?> interfaceReference : typeReference.getSuperInterfaces())
                {
                    queue.offer(interfaceReference);
                }
            }
        }
        return false;
    }

    private String getFullyQualifiedName(CtExecutableReference<?> executable)
    {
        return executable.getDeclaringType().getQualifiedName() + Method.CLASS_METHOD_NAME_DIVIDER + executable.getSimpleName();
    }
}
