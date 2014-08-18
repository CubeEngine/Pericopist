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
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;

public class CtUnaryOperatorExpressionConverter implements Converter<CtUnaryOperator<?>>
{
    @Override
    public Object convert(CtUnaryOperator<?> expression, ConverterManager manager) throws ConversionException
    {
        UnaryOperatorKind kind = expression.getKind();
        Object object = manager.convert(expression.getOperand());

        if (!(object instanceof Number))
        {
            throw new ConversionException(this, expression.getOperand(), "That kind of expression isn't supported for a unary operator expression.");
        }

        if (UnaryOperatorKind.POS.equals(kind))
        {
            // this unary operator kind doesn't change the object
            return object;
        }
        if (UnaryOperatorKind.POSTDEC.equals(kind))
        {
            // this unary operator kind doesn't change the object at this point
            return object;
        }
        if (UnaryOperatorKind.POSTINC.equals(kind))
        {
            // this unary operator kind doesn't change the object at this point
            return object;
        }

        throw new ConversionException(this, expression, "unary kind " + expression.getKind() + " isn't supported yet.");
    }
}
