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

import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;

/**
 * This converter is responsible for unary conditions like
 * <code>translate(-i)</code>
 */
class CtUnaryOperatorExpressionConverter implements Converter<CtUnaryOperator<?>>
{
    @Override
    public Object convert(CtUnaryOperator<?> expression, ConverterManager manager) throws ConversionException
    {
        UnaryOperatorKind kind = expression.getKind();
        Object object = manager.convert(expression.getOperand());
        if (object == null)
        {
            throw new ConversionException(this, expression, "The operand of the expression is null.");
        }

        switch (kind)
        {
            case POS:
            case POSTINC:
            case POSTDEC:
                if (object instanceof Number)
                {
                    // this unary operator kind doesn't change the object
                    return object;
                }
                break;

            case NEG:
                if (object instanceof Byte)
                {
                    return -(Byte) object;
                }
                if (object instanceof Integer)
                {
                    return -(Integer) object;
                }
                if (object instanceof Float)
                {
                    return -(Float) object;
                }
                if (object instanceof Double)
                {
                    return -(Double) object;
                }
                if (object instanceof Short)
                {
                    return -(Short) object;
                }
                if (object instanceof Long)
                {
                    return -(Long) object;
                }
                break;

            case NOT:
                if (object instanceof Boolean)
                {
                    return !(Boolean) object;
                }
                break;

            case COMPL:
                if (object instanceof Byte)
                {
                    return ~(Byte) object;
                }
                if (object instanceof Integer)
                {
                    return ~(Integer) object;
                }
                if (object instanceof Short)
                {
                    return ~(Short) object;
                }
                if (object instanceof Long)
                {
                    return ~(Long) object;
                }
                break;

            case PREINC:
                if (object instanceof Byte)
                {
                    return (Byte) object + 1;
                }
                if (object instanceof Integer)
                {
                    return (Integer) object + 1;
                }
                if (object instanceof Float)
                {
                    return (Float) object + 1;
                }
                if (object instanceof Double)
                {
                    return (Double) object + 1;
                }
                if (object instanceof Short)
                {
                    return (Short) object + 1;
                }
                if (object instanceof Long)
                {
                    return (Long) object + 1;
                }
                break;

            case PREDEC:
                if (object instanceof Byte)
                {
                    return (Byte) object - 1;
                }
                if (object instanceof Integer)
                {
                    return (Integer) object - 1;
                }
                if (object instanceof Float)
                {
                    return (Float) object - 1;
                }
                if (object instanceof Double)
                {
                    return (Double) object - 1;
                }
                if (object instanceof Short)
                {
                    return (Short) object - 1;
                }
                if (object instanceof Long)
                {
                    return (Long) object - 1;
                }
                break;

            default:
                throw new ConversionException(this, expression, "Unsupported unary operator kind " + kind.name());
        }
        throw new ConversionException(this, expression, "A " + object.getClass().getName() + " isn't supported for a " + kind.name() + " unary operator expression.");
    }
}
