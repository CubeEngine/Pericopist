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
package de.cubeisland.messageextractor.extractor.java.converter;

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtInvocation;
import spoon.support.reflect.code.CtFieldAccessImpl;

public class CtInvocationExpressionConverter implements Converter<CtInvocation> // TODO implement me
{
    @Override
    public String[] convert(CtInvocation expression, ConverterManager manager) throws ConversionException
    {
        System.out.println("Executable: " + expression.getExecutable());
        System.out.println("Parent: " + expression.getParent());
        System.out.println("arguments: " + expression.getArguments());
        System.out.println("label: " + expression.getLabel());
        System.out.println("signature: " + expression.getSignature());
        System.out.println("generic types: " + expression.getGenericTypes());
        System.out.println("parent field access: " + expression.getParent(CtFieldAccessImpl.class));
        System.out.println("type: " + expression.getType());
        System.out.println("target: " + expression.getTarget());
        System.out.println("target class: " + expression.getTarget().getClass());
        System.out.println("Actual Method: " + expression.getExecutable().getActualMethod());
        System.out.println();

        throw new ConversionException(this, expression, "The expression isn't supported yet.");
    }
}
