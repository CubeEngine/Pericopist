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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtExecutableReference;

/**
 * This converter is responsible for method invocations like
 * <code>translate("string".toUpperCase(Locale.ENGLISH)</code>
 */
public class CtInvocationExpressionConverter implements Converter<CtInvocation>
{
    @Override
    public Object[] convert(CtInvocation expression, ConverterManager manager) throws ConversionException
    {
        CtExecutableReference<?> exectuable = expression.getExecutable();

        Object target = null;
        Object[] arguments = new Object[expression.getArguments().size()];

        if(!exectuable.isStatic())
        {
            Object[] targets = manager.convert(expression.getTarget());

            if(targets == null || targets.length != 1)
            {
                throw new ConversionException(this, expression.getTarget(), "Couldn't load the target expression.");
            }

            target = targets[0];
        }
        for(int i = 0; i < arguments.length; i++)
        {
            Object[] argumentValues =  manager.convert((CtExpression) expression.getArguments().get(i));

            if(argumentValues == null || argumentValues.length != 1)
            {
                throw new ConversionException(this, (CtExpression) expression.getArguments().get(i), "Couldn't load the " + i + ". argument expression.");
            }

            arguments[i] = argumentValues[0];
        }

        AccessibleObject accessibleObject;
        if(exectuable.isConstructor())
        {
            accessibleObject = exectuable.getActualConstructor();
        }
        else
        {
            accessibleObject = exectuable.getActualMethod();
            if(((Method)accessibleObject).getReturnType() == null)
            {
                throw new ConversionException(this, expression, "The method doesn't have a return type");
            }
        }

        if(!accessibleObject.isAccessible())
        {
            accessibleObject.setAccessible(true);
        }

        if (accessibleObject instanceof Constructor<?>)
        {
            try
            {
                return new Object[] {((Constructor<?>)accessibleObject).newInstance(arguments)};
            }
            catch (InstantiationException | InvocationTargetException | IllegalAccessException e)
            {
                throw new ConversionException(this, expression, "", e);
            }
        }
        else
        {
            try
            {
                return new Object[] {((Method)accessibleObject).invoke(target, arguments)};
            }
            catch (IllegalAccessException | InvocationTargetException e)
            {
                throw new ConversionException(this, expression, "", e);
            }
        }
    }
}
