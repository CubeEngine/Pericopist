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
package de.cubeisland.messageextractor.extractor.java.converter.exception;

import de.cubeisland.messageextractor.exception.MessageExtractionException;
import de.cubeisland.messageextractor.extractor.java.converter.Converter;
import spoon.reflect.code.CtExpression;

/**
 * This exception is thrown when a conversion was not successful
 */
public class ConversionException extends MessageExtractionException
{
    private Converter<?> converter;
    private CtExpression<?> expression;

    public ConversionException(Converter<?> converter, CtExpression<?> expression, String msg)
    {
        super(buildMessage(converter, expression, msg));

        this.converter = converter;
        this.expression = expression;
    }

    public ConversionException(Converter<?> converter, CtExpression<?> expression, String msg, Throwable t)
    {
        super(buildMessage(converter, expression, msg), t);

        this.converter = converter;
        this.expression = expression;
    }

    public Converter<?> getConverter()
    {
        return converter;
    }

    public CtExpression<?> getExpression()
    {
        return expression;
    }

    private static String buildMessage(Converter<?> converter, CtExpression<?> expression, String message)
    {
        String msg = message;
        if(converter != null)
        {
            msg += "\nConverter: " + converter.getClass().getName();
        }
        msg += "\nConverting: " + String.valueOf(expression);
        if(expression != null)
        {
            msg += "\nConverting class: " + expression.getClass().getName();
            msg += "\nOccurrence: " + expression.getPosition().getFile().getPath();
            msg += ":" + expression.getPosition().getLine();
        }

        return msg;
    }
}
