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

import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;

/**
 * This converter is responsible for the concatenation of strings like
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
        if (leftHandOperand instanceof String || rightHandOperand instanceof String)
        {
            if (!BinaryOperatorKind.PLUS.equals(expression.getKind()))
            {
                throw new ConversionException(this, expression, "Just the '+' binary operator can be used for string operations. '" + expression.getKind().name() + "' isn't supported.");
            }

            return leftHandOperand.toString() + rightHandOperand.toString();
        }
        else
        {
            throw new ConversionException(this, expression, "A binary expression between " + leftHandOperand.getClass().getName() + " and " + rightHandOperand.getClass().getName() + " isn't supported yet.");
        }
    }
}
