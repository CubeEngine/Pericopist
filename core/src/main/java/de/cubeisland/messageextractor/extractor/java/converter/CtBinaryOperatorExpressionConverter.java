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

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.reference.CtTypeReference;

/**
 * This converter is responsible for binary expressions.
 * for example the concatenation of strings like
 * <code>translate("I'm concat" + "enated")</code>
 */
public class CtBinaryOperatorExpressionConverter implements Converter<CtBinaryOperator<?>>
{
    @Override
    public Object convert(CtBinaryOperator<?> expression, ConverterManager manager) throws ConversionException
    {
        Object[] leftHandOperandObjects = manager.convertToObjectArray(expression.getLeftHandOperand());
        if (leftHandOperandObjects == null || leftHandOperandObjects.length == 0)
        {
            return null;
        }

        Object[] rightHandOperandObjects = manager.convertToObjectArray(expression.getRightHandOperand());
        if (rightHandOperandObjects == null || rightHandOperandObjects.length == 0)
        {
            return null;
        }

        Object[] objects = new Object[leftHandOperandObjects.length * rightHandOperandObjects.length];

        for (int i = 0; i < leftHandOperandObjects.length; i++)
        {
            for (int j = 0; j < rightHandOperandObjects.length; j++)
            {
                objects[i * rightHandOperandObjects.length + j] = this.binaryOperation(expression, leftHandOperandObjects[i], rightHandOperandObjects[j]);
            }
        }

        return objects;
    }

    private Object binaryOperation(CtBinaryOperator<?> expression, Object leftHandOperand, Object rightHandOperand) throws ConversionException
    {
        BinaryOperatorKind kind = expression.getKind();
        switch (kind)
        {
            case OR:
                if (leftHandOperand instanceof Boolean && rightHandOperand instanceof Boolean)
                {
                    return (Boolean) leftHandOperand || (Boolean) rightHandOperand;
                }
                break;

            case AND:
                if (leftHandOperand instanceof Boolean && rightHandOperand instanceof Boolean)
                {
                    return (Boolean) leftHandOperand && (Boolean) rightHandOperand;
                }
                break;

            case BITOR:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.bitwiseOr(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case BITXOR:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.bitwiseXor(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case BITAND:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.bitwiseAnd(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case EQ:
                return leftHandOperand.equals(rightHandOperand);

            case NE:
                return !leftHandOperand.equals(rightHandOperand);

            case LT:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Boolean bool = this.lowerThan(leftHandOperand, rightHandOperand);
                    if (bool != null)
                    {
                        return bool;
                    }
                }
                break;

            case GT:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Boolean bool = this.greaterThan(leftHandOperand, rightHandOperand);
                    if (bool != null)
                    {
                        return bool;
                    }
                }
                break;

            case LE:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Boolean bool = this.lowerOrEqualThan(leftHandOperand, rightHandOperand);
                    if (bool != null)
                    {
                        return bool;
                    }
                }
                break;

            case GE:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Boolean bool = this.greaterOrEqualThan(leftHandOperand, rightHandOperand);
                    if (bool != null)
                    {
                        return bool;
                    }
                }
                break;

            case SL:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.shiftLeft(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case SR:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.shiftRight(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case USR:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.unsignedShiftRight(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case PLUS:
                if (leftHandOperand instanceof String || rightHandOperand instanceof String)
                {
                    return leftHandOperand.toString() + rightHandOperand.toString();
                }
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.addition(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case MINUS:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.subtraction(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case MUL:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.multiplication(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case DIV:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.division(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case MOD:
                if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
                {
                    Number number = this.modulo(leftHandOperand, rightHandOperand);
                    if (number != null)
                    {
                        return number;
                    }
                }
                break;

            case INSTANCEOF:
                if (rightHandOperand instanceof CtTypeReference)
                {
                    return ((CtTypeReference) rightHandOperand).getActualClass().isInstance(leftHandOperand);
                }
                break;

            default:
                throw new ConversionException(this, expression, "Unsupported binary operator kind " + kind.name());
        }
        throw new ConversionException(this, expression, "A " + kind.name() + " binary expression between " + leftHandOperand.getClass().getName() + " and " + rightHandOperand.getClass().getName() + " isn't supported.");
    }

    private Number bitwiseOr(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand | (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand | (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand | (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand | (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand | (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand | (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand | (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand | (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand | (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand | (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand | (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand | (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand | (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand | (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand | (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand | (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number bitwiseXor(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand ^ (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand ^ (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand ^ (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand ^ (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand ^ (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand ^ (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand ^ (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand ^ (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand ^ (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand ^ (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand ^ (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand ^ (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand ^ (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand ^ (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand ^ (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand ^ (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number bitwiseAnd(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand & (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand & (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand & (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand & (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand & (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand & (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand & (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand & (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand & (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand & (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand & (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand & (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand & (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand & (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand & (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand & (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Boolean lowerThan(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand < (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand < (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand < (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand < (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand < (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand < (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand < (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand < (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand < (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand < (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand < (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand < (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand < (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand < (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand < (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand < (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand < (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand < (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand < (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand < (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand < (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand < (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand < (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand < (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand < (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand < (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand < (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand < (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand < (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand < (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand < (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand < (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand < (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand < (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand < (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand < (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Boolean greaterThan(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand > (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand > (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand > (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand > (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand > (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand > (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand > (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand > (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand > (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand > (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand > (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand > (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand > (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand > (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand > (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand > (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand > (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand > (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand > (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand > (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand > (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand > (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand > (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand > (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand > (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand > (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand > (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand > (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand > (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand > (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand > (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand > (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand > (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand > (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand > (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand > (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Boolean lowerOrEqualThan(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand <= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand <= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand <= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand <= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand <= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand <= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand <= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand <= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand <= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand <= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand <= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand <= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand <= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand <= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand <= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand <= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand <= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand <= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand <= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand <= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand <= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand <= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand <= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand <= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand <= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand <= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand <= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand <= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand <= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand <= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand <= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand <= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand <= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand <= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand <= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand <= (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Boolean greaterOrEqualThan(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand >= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand >= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand >= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand >= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand >= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand >= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand >= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand >= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand >= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand >= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand >= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand >= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand >= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand >= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand >= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand >= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand >= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand >= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand >= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand >= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand >= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand >= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand >= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand >= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand >= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand >= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand >= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand >= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand >= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand >= (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand >= (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand >= (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand >= (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand >= (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand >= (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand >= (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number shiftLeft(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand << (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand << (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand << (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand << (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand << (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand << (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand << (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand << (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand << (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand << (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand << (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand << (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand << (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand << (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand << (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand << (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number shiftRight(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand >> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand >> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand >> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand >> (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand >> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand >> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand >> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand >> (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand >> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand >> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand >> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand >> (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand >> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand >> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand >> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand >> (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number unsignedShiftRight(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand >>> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand >>> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand >>> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand >>> (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand >>> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand >>> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand >>> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand >>> (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand >>> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand >>> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand >>> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand >>> (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand >>> (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand >>> (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand >>> (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand >>> (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number addition(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand + (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand + (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand + (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand + (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand + (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand + (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand + (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand + (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand + (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand + (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand + (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand + (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand + (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand + (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand + (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand + (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand + (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand + (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand + (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand + (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand + (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand + (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand + (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand + (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand + (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand + (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand + (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand + (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand + (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand + (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand + (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand + (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand + (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand + (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand + (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand + (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number subtraction(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand - (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand - (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand - (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand - (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand - (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand - (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand - (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand - (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand - (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand - (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand - (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand - (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand - (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand - (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand - (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand - (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand - (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand - (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand - (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand - (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand - (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand - (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand - (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand - (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand - (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand - (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand - (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand - (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand - (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand - (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand - (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand - (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand - (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand - (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand - (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand - (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number multiplication(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand * (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand * (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand * (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand * (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand * (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand * (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand * (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand * (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand * (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand * (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand * (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand * (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand * (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand * (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand * (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand * (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand * (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand * (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand * (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand * (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand * (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand * (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand * (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand * (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand * (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand * (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand * (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand * (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand * (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand * (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand * (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand * (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand * (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand * (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand * (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand * (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number division(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand / (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand / (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand / (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand / (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand / (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand / (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand / (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand / (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand / (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand / (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand / (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand / (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand / (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand / (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand / (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand / (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand / (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand / (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand / (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand / (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand / (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand / (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand / (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand / (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand / (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand / (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand / (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand / (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand / (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand / (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand / (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand / (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand / (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand / (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand / (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand / (Long) rightHandOperand;
            }
        }
        return null;
    }

    private Number modulo(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Byte)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Byte) leftHandOperand % (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte) leftHandOperand % (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Byte) leftHandOperand % (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Byte) leftHandOperand % (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte) leftHandOperand % (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte) leftHandOperand % (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer) leftHandOperand % (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer) leftHandOperand % (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Integer) leftHandOperand % (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Integer) leftHandOperand % (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer) leftHandOperand % (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer) leftHandOperand % (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Float)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Float) leftHandOperand % (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Float) leftHandOperand % (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Float) leftHandOperand % (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Float) leftHandOperand % (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Float) leftHandOperand % (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Float) leftHandOperand % (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Double)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Double) leftHandOperand % (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Double) leftHandOperand % (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Double) leftHandOperand % (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Double) leftHandOperand % (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Double) leftHandOperand % (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Double) leftHandOperand % (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short) leftHandOperand % (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short) leftHandOperand % (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Short) leftHandOperand % (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Short) leftHandOperand % (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short) leftHandOperand % (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short) leftHandOperand % (Long) rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long) leftHandOperand % (Byte) rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long) leftHandOperand % (Integer) rightHandOperand;
            }
            if (rightHandOperand instanceof Float)
            {
                return (Long) leftHandOperand % (Float) rightHandOperand;
            }
            if (rightHandOperand instanceof Double)
            {
                return (Long) leftHandOperand % (Double) rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long) leftHandOperand % (Short) rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long) leftHandOperand % (Long) rightHandOperand;
            }
        }
        return null;
    }
}
