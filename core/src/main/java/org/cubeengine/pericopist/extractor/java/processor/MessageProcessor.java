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
package org.cubeengine.pericopist.extractor.java.processor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.cubeengine.pericopist.exception.IllegalTranslatableMessageException;
import org.cubeengine.pericopist.extractor.java.configuration.JavaExpression;
import org.cubeengine.pericopist.extractor.java.configuration.JavaExtractorConfiguration;
import org.cubeengine.pericopist.extractor.java.converter.ConverterManager;
import org.cubeengine.pericopist.extractor.java.converter.exception.ConversionException;
import org.cubeengine.pericopist.message.MessageStore;
import org.cubeengine.pericopist.message.SourceReference;
import org.cubeengine.pericopist.message.TranslatableMessage;
import org.cubeengine.pericopist.util.Misc;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;

/**
 * A MessageProcessor is a {@link spoon.processing.Processor} which extracts translatable messages from java source code
 *
 * @param <E> the {@link spoon.reflect.declaration.CtElement} which shall be processed.
 */
public abstract class MessageProcessor<E extends CtElement> extends AbstractProcessor<E>
{
    private final JavaExtractorConfiguration configuration;
    private final MessageStore messageStore;
    private final ConverterManager converterManager;
    private final Logger logger;

    /**
     * The constructor "creates" a new MessageProcessor.
     *
     * @param configuration    the {@link org.cubeengine.pericopist.extractor.java.configuration.JavaExtractorConfiguration}
     * @param messageStore     the {@link org.cubeengine.pericopist.message.MessageStore} to which the messages shall be added
     * @param converterManager a {@link org.cubeengine.pericopist.extractor.java.converter.ConverterManager} which helps to convert CtElements
     * @param logger           the {@link java.util.logging.Logger} which logs messages
     */
    public MessageProcessor(JavaExtractorConfiguration configuration, MessageStore messageStore, ConverterManager converterManager, Logger logger)
    {
        this.configuration = configuration;
        this.messageStore = messageStore;
        this.converterManager = converterManager;
        this.logger = logger;
    }

    /**
     * This method returns the {@link org.cubeengine.pericopist.extractor.java.configuration.JavaExtractorConfiguration}
     *
     * @return the java extractor configuration
     */
    public JavaExtractorConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * This method returns the message store to which the messages shall be added
     *
     * @return {@link org.cubeengine.pericopist.message.MessageStore}
     */
    public MessageStore getMessageStore()
    {
        return messageStore;
    }

    /**
     * This method converts the specified expression with the {@link org.cubeengine.pericopist.extractor.java.converter.ConverterManager}
     *
     * @param expression     the expression which shall be converted
     * @param javaExpression the java expression from which the expression is converted.
     *
     * @return the converter messages or null if a conversion exception occurred
     */
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
            builder.append(javaExpression.getFQN());

            this.getLogger().log(Level.WARNING, builder.toString(), e.getCause());
        }
        return new String[0];
    }

    /**
     * This method adds a {@link org.cubeengine.pericopist.message.TranslatableMessage} to the {@link org.cubeengine.pericopist.message.MessageStore}.
     *
     * @param javaExpression the java expression from which the message was extraced
     * @param element        the element which occurs within the code
     * @param context        the message context
     * @param singulars      the message singulars
     * @param plurals        the message plurals
     */
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

    /**
     * This method extracts comments from the element, which start with '/// '.
     * A comment can be behind or above the element. The comment behind has a higher priority.
     *
     * @param element the element from which comments shall be extracted
     *
     * @return extracted comments
     */
    private String[] extractComments(E element)
    {
        SourcePosition position = element.getPosition();
        CompilationUnit compilationUnit = position.getCompilationUnit();
        String sourceCode = compilationUnit.getOriginalSourceCode();

        // load comment after element
        String comment = this.getExtractedComment(sourceCode.substring(position.getSourceEnd(), compilationUnit.nextLineIndex(position.getSourceEnd())), true);
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

            comment = this.getExtractedComment(sourceCode.substring(lastLineIndex, currentLineIndex), false);

            if (comment != null)
            {
                extractedComments.add(0, comment);
            }
        }
        while (comment != null);

        return extractedComments.toArray(new String[extractedComments.size()]);
    }

    /**
     * This method extracts a comments from the given line which start with '/// '.
     *
     * @param line the line
     *
     * @return the extracted comment or null
     */
    private String getExtractedComment(String line, boolean isBehindExpression)
    {
        line = line.trim();

        int index = line.indexOf("/// ");
        if (index < 0 || index != 0 && !isBehindExpression)
        {
            return null;
        }
        return line.substring(index + 4);
    }

    /**
     * This method returns the used {@link java.util.logging.Logger}
     *
     * @return the logger
     */
    public Logger getLogger()
    {
        return this.logger;
    }
}
