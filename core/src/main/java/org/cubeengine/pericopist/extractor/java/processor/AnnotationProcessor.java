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
package org.cubeengine.pericopist.extractor.java.processor;

import java.util.Arrays;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.cubeengine.pericopist.extractor.java.configuration.Annotation;
import org.cubeengine.pericopist.extractor.java.configuration.JavaExtractorConfiguration;
import org.cubeengine.pericopist.extractor.java.converter.ConverterManager;
import org.cubeengine.pericopist.message.MessageStore;
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
            CtExpression contextValue = element.getValue(annotation.getContextField());
            if (contextValue != null)
            {
                String[] contexts = this.getMessages(contextValue, annotation);
                if (contexts.length > 1)
                {
                    context = Arrays.toString(contexts);
                }
                else if (contexts.length == 1 && !contexts[0].isEmpty())
                {
                    context = contexts[0];
                }
            }
        }

        for (Entry<String, CtExpression> fieldEntry : element.getValues().entrySet())
        {
            if (!annotation.hasField(fieldEntry.getKey()))
            {
                continue;
            }

            String[] messages = this.getMessages((CtExpression<?>) fieldEntry.getValue(), annotation);
            if (messages.length == 0)
            {
                continue;
            }

            this.addMessage(annotation, element, context, messages, null);
        }
    }
}
