package de.cubeisland.maven.plugins.messagecatalog.message;

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
        if(cmp == 0)
        {
            return Integer.valueOf(this.line).compareTo(o.line);
        }
        return cmp;
    }

    @Override
    public String toString()
    {
        return this.file.getPath() + ":" + this.line;
    }
}
