package de.cubeisland.maven.plugins.messageextractor.message;

import java.io.File;
import java.util.Locale;

public class Occurrence implements Comparable<Occurrence>
{
    private final File file;
    private final int line;

    public Occurrence(File file, int line)
    {
        this.file = file;
        this.line = line;
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

    public int compareTo(Occurrence o)
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

        Occurrence that = (Occurrence)o;

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
        return this.file.getPath() + ":" + this.line;
    }
}
