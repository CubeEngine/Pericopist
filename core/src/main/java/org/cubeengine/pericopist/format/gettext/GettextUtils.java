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
package org.cubeengine.pericopist.format.gettext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.cubeengine.pericopist.message.SourceReference;
import org.cubeengine.pericopist.message.TranslatableExpression;
import org.cubeengine.pericopist.message.TranslatableMessage;

public final class GettextUtils
{
    private GettextUtils()
    {
    }

    /**
     * This method creates a List storing the extracted comments from the {@link TranslatableMessage}.
     * This is created with the help of the {@link SourceReference} instances which are stored by the
     * {@link TranslatableMessage}.
     * <p/>
     * Equal entries will be combined into one single entry containing the number of every entry
     * which is described by the entry.
     *
     * @param message {@link TranslatableMessage}
     *
     * @return extracted comments list
     */
    public static List<String> createExtractedComments(TranslatableMessage message)
    {
        List<SourceReferenceHolder> holders = combineSourceReferences(message.getSourceReferences());

        if (holders.isEmpty())
        {
            return Collections.emptyList();
        }

        List<String> extractedComments = new ArrayList<>();
        extractedComments.add("Extracted by:");

        for (SourceReferenceHolder holder : holders)
        {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < holder.numbers.size() - 1; i++)
            {
                builder.append(holder.numbers.get(i)).append(", ");
            }
            builder.append(holder.numbers.get(holder.numbers.size() - 1)).append(". ");
            if (holder.sourceReference.getExpression() != null)
            {
                TranslatableExpression expression = holder.sourceReference.getExpression();

                builder.append(expression.getClass().getSimpleName());
                builder.append("\n\tName: ").append(expression.getFQN());
                if (expression.getDescription() != null)
                {
                    builder.append("\n\tDescription: ").append(expression.getDescription());
                }
            }

            if (!holder.sourceReference.getExtractedComments().isEmpty())
            {
                builder.append("\n\tComments: ");
                for (String extractedComment : holder.sourceReference.getExtractedComments())
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

    /**
     * This method combines similar source reference entries to one single entry. It uses a helper class
     * which contains the source reference and and a list containing every number describing the entry.
     *
     * @param sourceReferences list of source references
     *
     * @return list of the helper class {@link SourceReferenceHolder}
     */
    private static List<SourceReferenceHolder> combineSourceReferences(Set<SourceReference> sourceReferences)
    {
        if (sourceReferences == null || sourceReferences.isEmpty())
        {
            return Collections.emptyList();
        }

        List<SourceReferenceHolder> holders = new ArrayList<>(sourceReferences.size());

        int number = 0;
        for (SourceReference reference : sourceReferences)
        {
            number++;
            int index = getSourceReferenceHolderIndex(holders, reference);

            if (index < 0)
            {
                SourceReferenceHolder holder = new SourceReferenceHolder();
                holder.sourceReference = reference;
                holder.numbers.add(number);
                holders.add(holder);
            }
            else
            {
                holders.get(index).numbers.add(number);
            }
        }

        return holders;
    }

    /**
     * A help message which is used in the {@link #combineSourceReferences(java.util.Set)} method. It loads the index of
     * the entry in the specified list which has the same source reference as described also.
     *
     * @param holders   list of the helper class {@link SourceReferenceHolder}
     * @param reference reference
     *
     * @return index of the specified reference in the list
     */
    private static int getSourceReferenceHolderIndex(List<SourceReferenceHolder> holders, SourceReference reference)
    {
        for (int i = 0; i < holders.size(); i++)
        {
            SourceReference sourceReference = holders.get(i).sourceReference;

            if (!sourceReference.getClass().equals(reference.getClass()))
            {
                continue;
            }

            if (sourceReference.getExpression() != null && !sourceReference.getExpression().equals(reference.getExpression()))
            {
                continue;
            }
            else if (sourceReference.getExpression() == null && reference.getExpression() != null)
            {
                continue;
            }

            if (!sourceReference.getExtractedComments().equals(reference.getExtractedComments()))
            {
                continue;
            }

            return i;
        }

        return -1;
    }

    /**
     * helper class which is used internal to store a source reference and a list of numbers which the
     * single entries have within the original source reference list
     */
    private static class SourceReferenceHolder
    {
        private SourceReference sourceReference;
        private final List<Integer> numbers;

        private SourceReferenceHolder()
        {
            this.numbers = new ArrayList<>(1);
        }
    }
}
