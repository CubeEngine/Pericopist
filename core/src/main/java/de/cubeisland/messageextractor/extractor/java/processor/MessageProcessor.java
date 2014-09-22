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
package de.cubeisland.messageextractor.extractor.java.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cubeisland.messageextractor.exception.IllegalTranslatableMessageException;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExpression;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.java.converter.ConverterManager;
import de.cubeisland.messageextractor.extractor.java.converter.exception.ConversionException;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.SourceReference;
import de.cubeisland.messageextractor.message.TranslatableMessage;
import de.cubeisland.messageextractor.util.Misc;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

public abstract class MessageProcessor<E extends CtElement> extends AbstractProcessor<E>
{
    private final JavaExtractorConfiguration configuration;
    private final MessageStore messageStore;
    private final ConverterManager converterManager;
    private final Logger logger;

    public MessageProcessor(JavaExtractorConfiguration configuration, MessageStore messageStore, ConverterManager converterManager, Logger logger)
    {
        this.configuration = configuration;
        this.messageStore = messageStore;
        this.converterManager = converterManager;
        this.logger = logger;
    }

    public JavaExtractorConfiguration getConfiguration()
    {
        return configuration;
    }

    public MessageStore getMessageStore()
    {
        return messageStore;
    }

    protected String[] getMessages(CtExpression<?> expression, JavaExpression javaExpression)
    {
        try
        {
            return this.converterManager.convertToStringArray(expression);
        }
        catch (ConversionException e)
        {
            StringBuilder builder = new StringBuilder("A conversion exception occurred.\n");
            builder.append(e.getClass().getName());
            builder.append('\n');
            builder.append(e.getMessage());
            builder.append("\nExpression: ");
            builder.append(expression.getParent().toString());
            builder.append("\nTranslatable-Expression-Type: ");
            builder.append(javaExpression.getClass().getSimpleName());
            builder.append("\nTranslatable-Expression-Name: ");
            builder.append(javaExpression);

            this.getLogger().log(Level.WARNING, builder.toString(), e.getCause());
        }
        return null;
    }

    protected void addMessage(JavaExpression javaExpression, E element, String context, String[] singulars, String[] plurals)
    {
        File file = Misc.getRelativizedFile(this.getConfiguration().getDirectory(), element.getPosition().getFile());
        SourceReference sourceReference = new SourceReference(file, element.getPosition().getLine(), javaExpression);

        if (context == null)
        {
            context = javaExpression.getDefaultContext();
        }

        for (String extractedComment : this.extractComments(element))
        {
            sourceReference.addExtractedComment(extractedComment);
        }

        if (javaExpression.hasPlural())
        {
            if (plurals.length > 1)
            {
                throw new IllegalTranslatableMessageException("A message can't have more than one plurals.");
            }

            if (singulars.length > 1)
            {
                throw new IllegalTranslatableMessageException("A message with a plural can't have more than one singular.");
            }

            if (singulars[0].isEmpty())
            {
                this.getLogger().info("The singular message can't be an empty string. Occurrence: " + sourceReference);
                return;
            }

            TranslatableMessage pluralMessage = this.getMessageStore().getMessage(context, singulars[0], plurals[0]);
            if (pluralMessage == null)
            {
                pluralMessage = new TranslatableMessage(context, singulars[0], plurals[0]);
                this.getMessageStore().addMessage(pluralMessage);
            }

            pluralMessage.addSourceReference(sourceReference);

            return;
        }

        for (String singular : singulars)
        {
            if (singular.isEmpty())
            {
                this.getLogger().info("The singular message can't be an empty string. Occurrence: " + sourceReference);
                continue;
            }

            TranslatableMessage singularMessage = this.getMessageStore().getMessage(context, singular, null);
            if (singularMessage == null)
            {
                singularMessage = new TranslatableMessage(context, singular, null);
                this.getMessageStore().addMessage(singularMessage);
            }

            singularMessage.addSourceReference(sourceReference);
        }
    }

    private String[] extractComments(E element)
    {
        SourcePosition position = element.getPosition();
        CompilationUnit compilationUnit = position.getCompilationUnit();
        String sourceCode = compilationUnit.getOriginalSourceCode();

        // load comment after element
        String comment = this.getExtractedComment(sourceCode.substring(position.getSourceEnd(), compilationUnit.nextLineIndex(position.getSourceEnd())));
        if (comment != null)
        {
            return new String[] {comment};
        }

        // load more line comments before element
        List<String> extractedComments = new ArrayList<>(1);

        int currentLineIndex;
        int lastLineIndex = compilationUnit.beginOfLineIndex(position.getSourceStart());

        do
        {
            currentLineIndex = lastLineIndex;
            lastLineIndex = compilationUnit.beginOfLineIndex(currentLineIndex - 2);

            comment = this.getExtractedComment(sourceCode.substring(lastLineIndex, currentLineIndex));

            if (comment != null)
            {
                extractedComments.add(0, comment);
            }
        }
        while (comment != null);

        return extractedComments.toArray(new String[extractedComments.size()]);
    }

    private String getExtractedComment(String line)
    {
        line = line.trim();

        int index = line.indexOf("/// ");
        if (index < 0)
        {
            return null;
        }
        return line.substring(index + 4);
    }

    public Logger getLogger()
    {
        return logger;
    }
}
