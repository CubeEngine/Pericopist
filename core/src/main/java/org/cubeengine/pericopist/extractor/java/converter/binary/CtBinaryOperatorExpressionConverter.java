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

import java.util.HashMap;
import java.util.Map;
import org.cubeengine.pericopist.extractor.java.converter.Converter;
import org.cubeengine.pericopist.extractor.java.converter.ConverterManager;
import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;

/**
 * This converter is responsible for binary expressions.
 * for example the concatenation of strings like
 * <code>translate("I'm concat" + "enated")</code>
 */
public class CtBinaryOperatorExpressionConverter implements Converter<CtBinaryOperator<?>>
{
    private Map<BinaryOperatorKind, BinaryOperation> binaryOperationMap;

    public CtBinaryOperatorExpressionConverter()
    {
        this.binaryOperationMap = new HashMap<>();

        this.registerBinaryOperation(new AndBinaryOperation(this));
        this.registerBinaryOperation(new BitwiseAndBinaryOperation(this));
        this.registerBinaryOperation(new BitwiseOrBinaryOperation(this));
        this.registerBinaryOperation(new BitwiseXorBinaryOperation(this));
        this.registerBinaryOperation(new DivisionBinaryOperation(this));
        this.registerBinaryOperation(new EqualsBinaryOperation(this));
        this.registerBinaryOperation(new GreaterOrEqualThanBinaryOperation(this));
        this.registerBinaryOperation(new GreaterThanBinaryOperation(this));
        this.registerBinaryOperation(new InstanceOfBinaryOperation(this));
        this.registerBinaryOperation(new LowerOrEqualThanBinaryOperation(this));
        this.registerBinaryOperation(new LowerThanBinaryOperation(this));
        this.registerBinaryOperation(new ModuleBinaryOperation(this));
        this.registerBinaryOperation(new MultiplicationBinaryOperation(this));
        this.registerBinaryOperation(new NotEqualsBinaryOperation(this));
        this.registerBinaryOperation(new OrBinaryOperation(this));
        this.registerBinaryOperation(new PlusBinaryOperation(this));
        this.registerBinaryOperation(new ShiftLeftBinaryOperation(this));
        this.registerBinaryOperation(new ShiftRightBinaryOperation(this));
        this.registerBinaryOperation(new SubtractionBinaryOperation(this));
        this.registerBinaryOperation(new UnsignedShiftRightBinaryOperation(this));
    }

    private void registerBinaryOperation(BinaryOperation binaryOperation)
    {
        this.binaryOperationMap.put(binaryOperation.getOperator(), binaryOperation);
    }

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
        BinaryOperation operation = this.binaryOperationMap.get(expression.getKind());
        if (operation == null)
        {
            throw new ConversionException(this, expression, "Unsupported binary operator kind " + expression.getKind().name());
        }
        return operation.executeOperation(expression, leftHandOperand, rightHandOperand);
    }
}
