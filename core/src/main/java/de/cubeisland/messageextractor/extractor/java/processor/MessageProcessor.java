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

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.java.configuration.TranslatableExpression;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.Occurrence;
import de.cubeisland.messageextractor.util.Misc;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtFieldReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtFieldAccessImpl;
import spoon.support.reflect.code.CtLiteralImpl;

public abstract class MessageProcessor<E extends CtElement> extends AbstractProcessor<E>
{
    private final JavaExtractorConfiguration configuration;
    private final MessageStore messageStore;
    private final Logger logger;

    public MessageProcessor(JavaExtractorConfiguration configuration, MessageStore messageStore, Logger logger)
    {
        this.configuration = configuration;
        this.messageStore = messageStore;
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

    public void addMessage(TranslatableExpression translatableExpression, E element, String singular, String plural)
    {
        Occurrence occurrence = new Occurrence(Misc.getRelativizedFile(this.getConfiguration().getDirectory(), element.getPosition().getFile()), element.getPosition().getLine());

        if (singular == null)
        {
            StringBuilder builder = new StringBuilder("A translatable message couldn't be extracted.");
            builder.append("\n\tType: ");
            builder.append(translatableExpression.getClass().getSimpleName());
            builder.append("\n\tName: ");
            builder.append(translatableExpression.getName());
            builder.append("\n\tExpression: ");
            builder.append(element);
            builder.append("\n\tOccurrence: ");
            builder.append(occurrence);

            this.getLogger().warning(builder.toString());
            return;
        }

        if(singular.isEmpty())
        {
            this.getLogger().info("The singular message can't be an empty string. Occurrence: " + occurrence);
            return;
        }

        this.getMessageStore().addMessage(singular, plural, occurrence, translatableExpression.getDescription());
    }

    protected String getString(CtExpression<?> expression)
    {
        if (expression instanceof CtLiteralImpl<?>)
        {
            return (String) ((CtLiteralImpl<?>) expression).getValue();
        }
        else if (expression instanceof CtBinaryOperatorImpl<?>)
        {
            return this.getString((CtBinaryOperatorImpl<?>) expression);
        }
        else if (expression instanceof CtFieldAccessImpl<?>)
        {
            return this.getString((CtFieldAccessImpl<?>) expression);
        }

        this.getLogger().info("The expression '" + expression.getClass().getName() + "' isn't supported yet.");
        return null;
    }

    private String getString(CtBinaryOperatorImpl<?> expression)
    {
        StringBuilder value = new StringBuilder(2);

        if (!BinaryOperatorKind.PLUS.equals(expression.getKind()))
        {
            this.getLogger().warning("Just the '+' binary operator can be used for string operations. '" + expression.getKind().name() + "' isn't supported.");
            return null;
        }

        String string = this.getString(expression.getLeftHandOperand());
        if (string == null)
        {
            return null;
        }
        value.append(string);

        string = this.getString(expression.getRightHandOperand());
        if (string == null)
        {
            return null;
        }
        value.append(string);

        return value.toString();
    }

    private String getString(CtFieldAccessImpl<?> expression)
    {
        CtFieldReference<?> fieldReference = expression.getVariable();
        if (!fieldReference.isStatic())
        {
            this.getLogger().info("'" + expression.getClass().getName() + "' expressions which aren't static aren't supported.");
            return null;
        }

        Member member = fieldReference.getActualField();
        if (member == null || !(member instanceof Field))
        {
            return null;
        }

        Field field = (Field) member;

        try
        {
            if (!field.isAccessible())
            {
                field.setAccessible(true);
            }

            Object o = field.get(null);
            if(o != null)
            {
                return o.toString();
            }
        }
        catch (SecurityException e)
        {
            this.logger.log(Level.SEVERE, "The access level of the field '" + field.getName() + "' from class " + field.getDeclaringClass().getName() + "' couldn't be modified.", e);
        }
        catch (IllegalAccessException e)
        {
            this.logger.log(Level.SEVERE, "The expression '" + expression.getClass().getName() + "' couldn't be parsed. The field '" + field.getName() + "' of the class '" + field.getDeclaringClass().getName() + "' couldn't be accessed.", e);
        }
        return null;
    }

    public Logger getLogger()
    {
        return logger;
    }
}
