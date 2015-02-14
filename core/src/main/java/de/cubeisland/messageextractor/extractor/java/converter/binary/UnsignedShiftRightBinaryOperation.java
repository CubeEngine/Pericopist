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
package de.cubeisland.messageextractor.extractor.java.converter.binary;

import de.cubeisland.messageextractor.extractor.java.converter.Converter;
import spoon.reflect.code.BinaryOperatorKind;

class UnsignedShiftRightBinaryOperation extends BinaryOperation
{
    protected UnsignedShiftRightBinaryOperation(Converter<?> binaryConverter)
    {
        super(binaryConverter, BinaryOperatorKind.USR);
    }

    @Override
    public Object operate(Object leftHandOperand, Object rightHandOperand)
    {
        if (leftHandOperand instanceof Number && rightHandOperand instanceof Number)
        {
            Number number = this.unsignedShiftRight(leftHandOperand, rightHandOperand);
            if (number != null)
            {
                return number;
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
                return (Byte)leftHandOperand >>> (Byte)rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Byte)leftHandOperand >>> (Integer)rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Byte)leftHandOperand >>> (Short)rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Byte)leftHandOperand >>> (Long)rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Integer)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Integer)leftHandOperand >>> (Byte)rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Integer)leftHandOperand >>> (Integer)rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Integer)leftHandOperand >>> (Short)rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Integer)leftHandOperand >>> (Long)rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Short)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Short)leftHandOperand >>> (Byte)rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Short)leftHandOperand >>> (Integer)rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Short)leftHandOperand >>> (Short)rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Short)leftHandOperand >>> (Long)rightHandOperand;
            }
        }
        if (leftHandOperand instanceof Long)
        {
            if (rightHandOperand instanceof Byte)
            {
                return (Long)leftHandOperand >>> (Byte)rightHandOperand;
            }
            if (rightHandOperand instanceof Integer)
            {
                return (Long)leftHandOperand >>> (Integer)rightHandOperand;
            }
            if (rightHandOperand instanceof Short)
            {
                return (Long)leftHandOperand >>> (Short)rightHandOperand;
            }
            if (rightHandOperand instanceof Long)
            {
                return (Long)leftHandOperand >>> (Long)rightHandOperand;
            }
        }
        return null;
    }
}
