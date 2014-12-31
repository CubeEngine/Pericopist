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
package de.cubeisland.messageextractor.extractor.java.processor;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.logging.Logger;

import de.cubeisland.messageextractor.extractor.java.configuration.Annotation;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.java.converter.ConverterManager;
import de.cubeisland.messageextractor.message.MessageStore;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtAnnotation;

/**
 * This class processes {@link CtAnnotation} instances.
 */
public class AnnotationProcessor extends MessageProcessor<CtAnnotation<?>>
{
    public AnnotationProcessor(JavaExtractorConfiguration configuration, MessageStore messageStore, ConverterManager converterManager, Logger logger)
    {
        super(configuration, messageStore, converterManager, logger);
    }

    @Override
    public void process(CtAnnotation<?> element)
    {
        Annotation annotation = this.getConfiguration().getTranslatable(Annotation.class, element);
        if (annotation == null)
        {
            return;
        }

        String context = null;
        if (annotation.getContextField() != null)
        {
            Object contextValue = element.getElementValue(annotation.getContextField());
            if (contextValue != null)
            {
                if (contextValue.getClass().isArray() && !contextValue.getClass().getComponentType().isPrimitive())
                {
                    context = Arrays.toString((Object[]) contextValue);
                }
                else
                {
                    context = contextValue.toString();
                }

                if (context.isEmpty())
                {
                    context = null;
                }
            }
        }

        for (Entry<String, Object> fieldEntry : element.getElementValues().entrySet())
        {
            if (!annotation.hasField(fieldEntry.getKey()))
            {
                continue;
            }

            if (fieldEntry.getValue() instanceof CtExpression<?>)
            {
                String[] messages = this.getMessages((CtExpression<?>) fieldEntry.getValue(), annotation);
                if (messages.length == 0)
                {
                    continue;
                }

                this.addMessage(annotation, element, context, messages, null);
            }
        }
    }
}
