package de.cubeisland.cubeengine.util.gettextgen.parser;

import java.io.File;

public class Occurrence
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
}
