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
package de.cubeisland.messageextractor.format.gettext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.cubeisland.messageextractor.message.SourceReference;
import de.cubeisland.messageextractor.message.TranslatableExpression;
import de.cubeisland.messageextractor.message.TranslatableMessage;

public final class GettextUtils
{
    private GettextUtils()
    { }

    /**
     * This method creates a List storing the extracted comments from the TranslatableMessage.
     * This is created with the help of the SourceReference instances which are stored by the
     * TranslatableMessage.
     *
     * @param message TranslatableMessage
     *
     * @return extracted comments list
     */
    public static List<String> createExtractedComments(TranslatableMessage message)
    {
        if (message.getSourceReferences().isEmpty())
        {
            return Collections.emptyList();
        }

        List<String> extractedComments = new ArrayList<>();
        extractedComments.add("Extracted by:");

        int sourceReferenceCount = 0;
        for (SourceReference reference : message.getSourceReferences())
        {
            StringBuilder builder = new StringBuilder();
            builder.append(++sourceReferenceCount).append(". ");
            if (reference.getExpression() != null)
            {
                TranslatableExpression expression = reference.getExpression();

                builder.append(expression.getClass().getSimpleName());
                builder.append("\n\tName: ").append(expression.getFQN());
                if (expression.getDescription() != null)
                {
                    builder.append("\n\tDescription: ").append(expression.getDescription());
                }
            }

            if (!reference.getExtractedComments().isEmpty())
            {
                builder.append("\n\tComments: ");
                for (String extractedComment : reference.getExtractedComments())
                {
                    builder.append("\n\t- ").append(extractedComment);
                }
            }

            if (builder.indexOf("\n") != -1)
            {
                Collections.addAll(extractedComments, builder.toString().split("\n"));
            }
        }

        return extractedComments;
    }
}
