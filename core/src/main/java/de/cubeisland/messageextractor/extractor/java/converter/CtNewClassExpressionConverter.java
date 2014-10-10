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
package de.cubeisland.messageextractor.extractor.java.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.reference.CtExecutableReference;

/**
 * This converter is responsible for constructor calls
 * like <code>translate(new Classname())</code>
 */
public class CtNewClassExpressionConverter extends CtAbstractInvocationExpressionConverter<CtNewClass<?>>
{
    @Override
    public Object convert(CtNewClass<?> expression, ConverterManager manager) throws ConversionException
    {
        CtExecutableReference<?> executable = expression.getExecutable();

        // 1. load arguments
        Object[] arguments = this.loadArguments(expression, manager);

        // 2. load constructor
        Constructor<?> constructor = executable.getActualConstructor();

        // 3. set accessible
        this.setAccessible(constructor);

        // 4. create new instance
        try
        {
            return constructor.newInstance(arguments);
        }
        catch (IllegalAccessException | InvocationTargetException | InstantiationException e)
        {
            throw new ConversionException(this, expression, "A new instance couldn't be created.", e);
        }
    }
}
