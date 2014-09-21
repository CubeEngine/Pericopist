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
package de.cubeisland.messageextractor.message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SourceReference implements Comparable<SourceReference>
{
    private final File file;
    private final int line;
    private final TranslatableExpression expression;

    private final List<String> extractedComments;

    public SourceReference(File file, int line, TranslatableExpression expression)
    {
        this.file = file;
        this.line = line;
        this.expression = expression;

        this.extractedComments = new ArrayList<>();
    }

    public File getFile()
    {
        return file;
    }

    public String getPath()
    {
        return this.getFile().getPath().replaceAll("\\\\", "/");
    }

    public int getLine()
    {
        return line;
    }

    public TranslatableExpression getExpression()
    {
        return this.expression;
    }

    public void addExtractedComment(String comment)
    {
        this.extractedComments.add(comment);
    }

    public List<String> getExtractedComments()
    {
        return this.extractedComments;
    }

    @Override
    public int compareTo(SourceReference o)
    {
        int cmp = this.getFile().getPath().toLowerCase(Locale.ENGLISH).compareTo(o.getFile().getPath().toLowerCase(Locale.ENGLISH));
        if (cmp == 0)
        {
            cmp = Integer.valueOf(this.line).compareTo(o.line);

            if (cmp == 0)
            {
                return this.compareExpressions(o.getExpression());
            }
        }
        return cmp;
    }

    private int compareExpressions(TranslatableExpression o)
    {
        TranslatableExpression t = this.getExpression();
        if (t != null)
        {
            if (o != null)
            {
                return t.getFQN().compareTo(o.getFQN());
            }
            return 1;
        }
        else if (o != null)
        {
            return -1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        SourceReference that = (SourceReference) o;

        if (this.line != that.line)
        {
            return false;
        }
        if (!this.file.equals(that.file))
        {
            return false;
        }
        if (!this.extractedComments.equals(that.extractedComments))
        {
            return false;
        }

        if (this.getExpression() == null)
        {
            return that.getExpression() == null;
        }
        return this.getExpression().equals(that.getExpression());
    }

    @Override
    public int hashCode()
    {
        int result = this.file.hashCode();
        result = 31 * result + this.line;
        result = 31 * result + this.extractedComments.hashCode();
        result = 31 * result + (this.getExpression() != null ? this.getExpression().hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return this.getPath() + ":" + this.line;
    }
}
