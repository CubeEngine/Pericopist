package de.cubeisland.maven.plugins.messagecatalog.message;

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
        return this.toString().compareTo(o.toString());
    }

    @Override
    public String toString()
    {
        return this.file.getPath() + ":" + this.line;
    }
}
