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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.reference.CtFieldReference;

/**
 * This converter is responsible for constant expressions like
 * <code>translate(CLASS.CONSTANT)</code>
 */
class CtFieldAccessExpressionConverter implements Converter<CtFieldAccess<?>>
{
    @Override
    public Object convert(CtFieldAccess<?> expression, ConverterManager manager) throws ConversionException
    {
        Field field = this.getField(expression);
        try
        {
            if (!field.isAccessible())
            {
                field.setAccessible(true);
            }

            return field.get(null);
        }
        catch (SecurityException e)
        {
            throw new ConversionException(this, expression, "The access level of the field '" + field.getName() + "' from class " + field.getDeclaringClass().getName() + "' couldn't be modified.", e);
        }
        catch (IllegalAccessException e)
        {
            throw new ConversionException(this, expression, "The expression '" + expression.getClass().getName() + "' couldn't be parsed. The field '" + field.getName() + "' of the class '" + field.getDeclaringClass().getName() + "' couldn't be accessed.", e);
        }
    }

    /**
     * Returns the java field of the specified expression
     *
     * @param expression field access expression
     *
     * @return field
     */
    private Field getField(CtFieldAccess<?> expression) throws ConversionException
    {
        CtFieldReference<?> fieldReference = expression.getVariable();
        if (!fieldReference.isStatic())
        {
            throw new ConversionException(this, expression, "'" + expression.getClass().getName() + "' expressions which aren't static aren't supported.");
        }

        Member member = fieldReference.getActualField();
        if (!(member instanceof Field))
        {
            throw new ConversionException(this, expression, "The member isn't a field value");
        }

        return (Field)member;
    }
}
