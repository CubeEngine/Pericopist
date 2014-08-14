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

import java.lang.reflect.AccessibleObject;

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtExpression;

public abstract class CtAbstractInvocationExpressionConverter<T extends CtExpression> implements Converter<T>
{
    protected Object[] loadArguments(T expression, ConverterManager manager) throws ConversionException
    {
        CtAbstractInvocation<?> abstractInvocation = (CtAbstractInvocation<?>) expression;
        Object[] arguments = new Object[abstractInvocation.getArguments().size()];

        for(int i = 0; i < arguments.length; i++)
        {
            Object[] argumentValues =  manager.convert((CtExpression) abstractInvocation.getArguments().get(i));

            if(argumentValues == null || argumentValues.length != 1)
            {
                throw new ConversionException(this, (CtExpression) abstractInvocation.getArguments().get(i), "Couldn't load the " + i + ". argument expression.");
            }

            arguments[i] = argumentValues[0];
        }

        return arguments;
    }

    protected void setAccessible(AccessibleObject accessibleObject)
    {
        if(!accessibleObject.isAccessible())
        {
            accessibleObject.setAccessible(true);
        }
    }
}
