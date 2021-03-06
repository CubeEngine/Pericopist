/*
 * The MIT License
 * Copyright © 2013 Cube Island
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtConditional;

/**
 * This converter is responsible for conditions like
 * <code>translate(really? "yes" : "no")</code>
 */
class CtConditionalExpressionConverter implements Converter<CtConditional<?>>
{
    @Override
    public Object convert(CtConditional<?> expression, ConverterManager manager) throws ConversionException
    {
        Set<Object> objects = new HashSet<>();

        Object[] conditionObjects = manager.convertToObjectArray(expression.getThenExpression());
        if (conditionObjects != null)
        {
            Collections.addAll(objects, conditionObjects);
        }

        conditionObjects = manager.convertToObjectArray(expression.getElseExpression());
        if (conditionObjects != null)
        {
            Collections.addAll(objects, conditionObjects);
        }

        return objects.toArray(new Object[0]);
    }
}
