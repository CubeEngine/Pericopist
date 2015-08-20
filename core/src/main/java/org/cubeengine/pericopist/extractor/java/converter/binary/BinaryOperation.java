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
package org.cubeengine.pericopist.extractor.java.converter.binary;

import org.cubeengine.pericopist.extractor.java.converter.Converter;
import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;

abstract class BinaryOperation
{
    private final Converter<?> binaryConverter;
    private final BinaryOperatorKind operator;

    BinaryOperation(Converter<?> binaryConverter, BinaryOperatorKind operator)
    {
        this.binaryConverter = binaryConverter;
        this.operator = operator;
    }

    public final BinaryOperatorKind getOperator()
    {
        return this.operator;
    }

    public final Object executeOperation(CtBinaryOperator<?> expression, Object leftHandOperand, Object rightHandOperand) throws ConversionException
    {
        try
        {
            Object result = this.operate(leftHandOperand, rightHandOperand);
            if (result != null)
            {
                return result;
            }
        }
        catch (Exception e)
        {
            throw new ConversionException(this.binaryConverter, expression, String.format("The %s binary expression couldn't be performed.", this.getOperator().name()), e);
        }

        throw new ConversionException(this.binaryConverter, expression, String.format("A %s binary expression between %s and %s isn't supported.", this.getOperator().name(),
                                                                                      leftHandOperand.getClass().getName(), rightHandOperand.getClass().getName()));
    }

    protected abstract Object operate(Object leftHandOperand, Object rightHandOperand);
}
