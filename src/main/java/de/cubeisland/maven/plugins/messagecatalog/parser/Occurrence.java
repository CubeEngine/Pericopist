package de.cubeisland.maven.plugins.messagecatalog.parser;

import java.io.File;

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

    public int getLine()
    {
        return line;
    }

    public int compareTo(Occurrence o)
    {
        int value = this.file.getAbsolutePath().compareTo(o.file.getAbsolutePath());
        if(value == 0)
        {
            value = Integer.compare(this.line, o.line);
        }
        return value;
    }
}
