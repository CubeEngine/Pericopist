package de.cubeisland.maven.plugins.messagecatalog.message;

public class Occurrence implements Comparable<Occurrence>
{
    private final String path;
    private final int line;

    public Occurrence(String path, int line)
    {
        this.path = path;
        this.line = line;
    }

    public String getPath()
    {
        return path;
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
        return this.path + ":" + this.line;
    }
}
