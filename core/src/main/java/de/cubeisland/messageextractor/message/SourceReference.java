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
import java.util.Locale;

public class SourceReference implements Comparable<SourceReference>
{
    private final File file;
    private final int line;
    private final TranslatableExpression expression;

    public SourceReference(File file, int line, TranslatableExpression expression)
    {
        this.file = file;
        this.line = line;
        this.expression = expression;
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

    // TODO everthing below

    @Override
    public int compareTo(SourceReference o)
    {
        int cmp = this.getFile().getPath().toLowerCase(Locale.ENGLISH).compareTo(o.getFile().getPath().toLowerCase(Locale.ENGLISH));
        if (cmp == 0)
        {
            return Integer.valueOf(this.line).compareTo(o.line);
        }
        return cmp;
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

        if (line != that.line)
        {
            return false;
        }
        return file.equals(that.file);
    }

    @Override
    public int hashCode()
    {
        int result = file.hashCode();
        result = 31 * result + line;
        return result;
    }

    @Override
    public String toString()
    {
        return this.getPath() + ":" + this.line;
    }
}
