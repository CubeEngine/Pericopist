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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.reference.CtExecutableReference;

/**
 * This converter is responsible for method invocations like
 * <code>translate("string".toUpperCase(Locale.ENGLISH)</code>
 */
public class CtInvocationExpressionConverter extends CtAbstractInvocationExpressionConverter<CtInvocation>
{
    @Override
    public Object convert(CtInvocation expression, ConverterManager manager) throws ConversionException
    {
        CtExecutableReference<?> executable = expression.getExecutable();

        // 1. load target if executable isn't static
        Object target = null;
        if(!executable.isStatic())
        {
            target = manager.convert(expression.getTarget());

            if(target == null || target.getClass().isArray())
            {
                throw new ConversionException(this, expression.getTarget(), "Couldn't load the target expression.");
            }
        }

        // 2. load arguments
        Object[] arguments = this.loadArguments(expression, manager);

        // 3. load method and check whether it has a return type
        Method method = executable.getActualMethod();
        if(method.getReturnType() == null)
        {
            throw new ConversionException(this, expression, "The method doesn't have a return type");
        }

        // 4. set accessible
        this.setAccessible(method);

        // 5. invoke method
        try
        {
            return method.invoke(target, arguments);
        }
        catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e)
        {
            throw new ConversionException(this, expression, "The method couldn't be invoked.", e);
        }
    }
}
