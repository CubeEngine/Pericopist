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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import de.cubeisland.messageextractor.extractor.java.converter.exception.ConverterNotFoundException;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;

/**
 * This class manages all converters which convert a <code>CtExpression</code> into
 * a <code>String[]</code>
 */
public class ConverterManager
{
    private Map<Class<? extends CtExpression>, Converter> converters;

    public ConverterManager(boolean registerDefaultConverter)
    {
        this.converters = new HashMap<>();

        if (registerDefaultConverter)
        {
            this.registerDefaultConverter();
        }
    }

    /**
     * This method registers a converter for the specified class
     *
     * @param clazz     the class
     * @param converter the converter
     */
    public void registerConverter(Class<? extends CtExpression> clazz, Converter converter)
    {
        if (clazz == null || converter == null)
        {
            return;
        }
        this.converters.put(clazz, converter);
    }

    /**
     * This method returns the converter which is related to the specified class
     *
     * @param clazz the class
     *
     * @return the converter
     */
    public Converter getConverter(Class<? extends CtExpression> clazz)
    {
        return this.converters.get(clazz);
    }

    /**
     * This method returns the converter which is assignable from the specified class
     *
     * @param clazz the class
     *
     * @return the converter
     */
    public Converter findConverter(Class<? extends CtExpression> clazz)
    {
        for (Entry<Class<? extends CtExpression>, Converter> entry : this.converters.entrySet())
        {
            if (entry.getKey().isAssignableFrom(clazz))
            {
                Converter converter = entry.getValue();
                this.registerConverter(clazz, converter);
                return converter;
            }
        }
        return null;
    }

    /**
     * This method matches a registered converter
     *
     * @param clazz the class to match for
     *
     * @return a matching converter
     */
    @SuppressWarnings("unchecked")
    public <T extends CtExpression> Converter<T> matchConverter(Class<? extends T> clazz) throws ConverterNotFoundException
    {
        Converter converter = this.getConverter(clazz);
        if (converter == null)
        {
            converter = this.findConverter(clazz);
        }
        if (converter != null)
        {
            return converter;
        }
        throw new ConverterNotFoundException("Converter not found for: " + clazz.getName());
    }

    /**
     * This method converts an expression into a string array
     *
     * @param expression the expression
     *
     * @return the string array
     */
    public <T extends CtExpression<?>> String[] convert(T expression) throws ConversionException
    {
        if (expression == null)
        {
            return null;
        }
        return this.matchConverter(expression.getClass()).convert(expression, this);
    }

    private void registerDefaultConverter()
    {
        this.registerConverter(CtBinaryOperator.class, new CtBinaryOperatorExpressionConverter());
        this.registerConverter(CtConditional.class, new CtConditionalExpressionConverter());
        this.registerConverter(CtFieldAccess.class, new CtFieldAccessExpressionConverter());
        //        this.registerConverter(CtInvocation.class, new CtInvocationExpressionConverter());
        this.registerConverter(CtLiteral.class, new CtLiteralExpressionConverter());
        this.registerConverter(CtNewArray.class, new CtNewArrayExpressionConverter());
    }
}
