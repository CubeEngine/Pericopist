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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import de.cubeisland.messageextractor.extractor.java.converter.exception.ConverterNotFoundException;
import de.cubeisland.messageextractor.util.Misc;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtUnaryOperator;

/**
 * This class manages all converters which convert a <code>CtExpression</code> into
 * an <code>Object</code>.
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
     * @param expression the expression to match for
     *
     * @return a matching converter
     */
    @SuppressWarnings("unchecked")
    public <T extends CtExpression> Converter<T> matchConverter(CtExpression expression) throws ConverterNotFoundException
    {
        Converter converter = this.getConverter(expression.getClass());
        if (converter == null)
        {
            converter = this.findConverter(expression.getClass());
        }
        if (converter != null)
        {
            return converter;
        }
        throw new ConverterNotFoundException(expression);
    }

    /**
     * This method converts an expression into a string array
     *
     * @param expression the expression
     *
     * @return the string array
     */
    public <T extends CtExpression<?>> String[] convertToStringArray(T expression) throws ConversionException
    {
        Object[] objects = this.convertToObjectArray(expression);

        if (objects == null)
        {
            return null;
        }

        String[] strings = new String[objects.length];
        for (int i = 0; i < objects.length; i++)
        {
            strings[i] = objects[i].toString();
        }
        return strings;
    }

    /**
     * This method converts an expression into a object
     *
     * @param expression the expression
     *
     * @return the object
     */
    public <T extends CtExpression<?>> Object convert(T expression) throws ConversionException
    {
        if (expression == null)
        {
            return null;
        }
        return this.matchConverter(expression).convert(expression, this);
    }

    /**
     * This method converts an expression into an Object array
     *
     * @param expression the expression
     *
     * @return the Object array
     */
    public <T extends CtExpression<?>> Object[] convertToObjectArray(T expression) throws ConversionException
    {
        Object o = this.convert(expression);

        if (o == null)
        {
            return (Object[]) Array.newInstance(Object.class, 1);
        }
        if (o.getClass().isArray())
        {
            return this.toObjectArray(o);
        }

        Object array = Array.newInstance(o.getClass(), 1);
        Array.set(array, 0, o);
        return (Object[]) array;
    }

    private void registerDefaultConverter()
    {
        this.registerConverter(CtBinaryOperator.class, new CtBinaryOperatorExpressionConverter());
        this.registerConverter(CtConditional.class, new CtConditionalExpressionConverter());
        this.registerConverter(CtFieldAccess.class, new CtFieldAccessExpressionConverter());
        this.registerConverter(CtInvocation.class, new CtInvocationExpressionConverter());
        this.registerConverter(CtLiteral.class, new CtLiteralExpressionConverter());
        this.registerConverter(CtNewArray.class, new CtNewArrayExpressionConverter());
        this.registerConverter(CtNewClass.class, new CtNewClassExpressionConverter());
        this.registerConverter(CtUnaryOperator.class, new CtUnaryOperatorExpressionConverter());
    }

    /**
     * This method converts the specified array into an object array.
     *
     * @param array array object
     *
     * @return the object array
     */
    private Object[] toObjectArray(Object array)
    {
        Class<?> componentType = this.getLastComponentType(array);
        if (!componentType.isPrimitive())
        {
            // Array doesn't have to be converted
            return (Object[]) array;
        }

        // primitive arrays must be converted to object arrays
        componentType = this.toComponentType(componentType, this.getDimensionCount(array));
        Object convertedArray = Array.newInstance(componentType, Array.getLength(array));

        for (int i = 0; i < Array.getLength(array); i++)
        {
            Object element = Array.get(array, i);
            if (!element.getClass().isArray())
            {
                Array.set(convertedArray, i, element);
            }
            else
            {
                Array.set(convertedArray, i, this.toObjectArray(element));
            }
        }

        return (Object[]) convertedArray;
    }

    /**
     * This method returns the component type of the array.
     *
     * @param clazz     the component class
     * @param dimension the dimension of the array
     *
     * @return the component type of the array
     */
    private Class<?> toComponentType(Class<?> clazz, int dimension)
    {
        Class<?> objectClass = Misc.getRelatedClass(clazz);
        for (int i = 1; i < dimension; i++)
        {
            Object array = Array.newInstance(objectClass, 0);
            objectClass = array.getClass();
        }
        return objectClass;
    }

    /**
     * This method returns the last component type of the array hierarchy
     *
     * @param array array object
     *
     * @return last component type
     */
    private Class<?> getLastComponentType(Object array)
    {
        Class<?> clazz = array.getClass();
        while (clazz.isArray())
        {
            clazz = clazz.getComponentType();
        }
        return clazz;
    }

    /**
     * This method returns the dimension of an array
     *
     * @param array array object
     *
     * @return dimension
     */
    private int getDimensionCount(Object array)
    {
        int dimension = 0;
        Class<?> clazz = array.getClass();
        while (clazz.isArray())
        {
            dimension++;
            clazz = clazz.getComponentType();
        }
        return dimension;
    }
}
