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
package org.cubeengine.pericopist.extractor.java.converter;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.List;

import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtExpression;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

public abstract class CtAbstractInvocationExpressionConverter<T extends CtExpression> implements Converter<T>
{
    protected Object[] loadArguments(T expression, ConverterManager manager) throws ConversionException
    {
        CtAbstractInvocation<?> abstractInvocation = (CtAbstractInvocation<?>) expression;
        List<CtTypeReference<?>> parameters = abstractInvocation.getExecutable().getParameters();

        if(parameters.isEmpty())
        {
            return new Object[0];
        }

        Object[] arguments = new Object[abstractInvocation.getArguments().size()];
        for(int i = 0; i < arguments.length; i++)
        {
            arguments[i] =  manager.convert(abstractInvocation.getArguments().get(i));
        }

        if(matchesParameter(abstractInvocation.getArguments(), parameters))
        {
            return arguments;
        }

        Object[] parameterValues = new Object[parameters.size()];
        System.arraycopy(arguments, 0, parameterValues, 0, parameterValues.length - 1);

        // contains every entry from the arguments which has the index index param.length - 1
        Object[] lastValues = new Object[arguments.length - parameters.size() + 1];
        System.arraycopy(arguments, parameters.size() - 1, lastValues, 0, lastValues.length);

        parameterValues[parameterValues.length - 1] = this.createArray(((CtArrayTypeReference) parameters.get(parameters.size() - 1)).getComponentType().getActualClass(), lastValues);

        return parameterValues;
    }

    private boolean matchesParameter(List<CtExpression<?>> arguments, List<CtTypeReference<?>> parameters)
    {
        if(arguments.size() != parameters.size())
        {
            return false;
        }

        CtTypeReference<?> lastParameterType = parameters.get(parameters.size() - 1);
        CtTypeReference<?> lastArgumentType = arguments.get(arguments.size() - 1).getType();
        do
        {
            if (!(lastParameterType instanceof CtArrayTypeReference))
            {
                return true;
            }
            if (!(lastArgumentType instanceof CtArrayTypeReference))
            {
                return false;
            }

            lastParameterType = ((CtArrayTypeReference) lastParameterType).getComponentType();
            lastArgumentType = ((CtArrayTypeReference) lastArgumentType).getComponentType();
        }
        while (true);
    }

    private Object createArray(Class<?> componentType, Object[] oldArray)
    {
        Object array = Array.newInstance(componentType, oldArray.length);
        for(int i = 0; i < oldArray.length; i++)
        {
            Array.set(array, i, oldArray[i]);
        }
        return array;
    }

    protected void setAccessible(AccessibleObject accessibleObject)
    {
        if(!accessibleObject.isAccessible())
        {
            accessibleObject.setAccessible(true);
        }
    }
}
