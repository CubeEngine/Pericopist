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

import javax.xml.bind.annotation.XmlRootElement;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;

/**
 * The constructor describes a new object generation.
 * It can be specified with the xml used for the JavaExtractorConfiguration.
 * <p/>
 * Example:
 * <p/>
 * <pre>
 * {@code
 * <constructor>
 *     <name>I.am.the.class</name>
 *     <signature>
 *         <type use-as="singular">java.lang.String</type>
 *         <type>java.lang.Object[]</type>
 *     </signature>
 *     <description>I am a default context</description>
 * </constructor>
 * }
 * </pre>
 *
 * @see de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration
 */
@XmlRootElement(name = "constructor")
public class Constructor extends CallableExpression
{
    @Override
    public boolean matches(CtElement element)
    {
        if (!(element instanceof CtAbstractInvocation<?>))
        {
            return false;
        }

        CtExecutableReference<?> executable = ((CtAbstractInvocation<?>) element).getExecutable();
        if(executable == null)
        {
            return false;
        }

        if (!executable.isConstructor())
        {
            return false;
        }

        if (!this.getName().equals(executable.getDeclaringType().getQualifiedName()))
        {
            return false;
        }

        return this.matchesSignature(executable);
    }
}
