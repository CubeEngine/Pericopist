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

import java.lang.reflect.Array;
import java.util.List;

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;

/**
 * This converter is responsible for new array expressions like
 * <code>@Annotation({"first", "second", "third"})</code>
 */
public class CtNewArrayExpressionConverter implements Converter<CtNewArray<?>>
{
    @Override
    public Object convert(CtNewArray<?> expression, ConverterManager manager) throws ConversionException
    {
        List<CtExpression<?>> elements = expression.getElements();
        CtTypeReference<?> type = ((CtArrayTypeReference)expression.getType()).getComponentType();

        Object array = Array.newInstance(type.getActualClass(), elements.size());

        for(int i = 0; i < elements.size(); i++)
        {
            Array.set(array, i, manager.convert(elements.get(i)));
        }

        return array;
    }
}
