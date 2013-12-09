package de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables;

public class TranslatableMethod
{
    private String name;
    private int singularIndex;
    private int pluralIndex;

    public TranslatableMethod(String method)
    {
        String[] parts = method.trim().split(":");

        this.name = parts[0];
        this.singularIndex = 1;
        this.pluralIndex = -1;

        if(parts.length > 1)
        {
            String[] lines = parts[1].split(",");
            this.singularIndex = Integer.valueOf(lines[0]);
            if(lines.length > 1)
            {
                this.pluralIndex = Integer.valueOf(lines[1]);
            }
        }
    }

    public String getName()
    {
        return this.name;
    }

    public int getSingularIndex()
    {
        return this.singularIndex - 1;
    }

    public boolean hasPlural()
    {
        return this.pluralIndex != -1;
    }

    public int getPluralIndex()
    {
        return this.pluralIndex - 1;
    }

    @Override
    public String toString()
    {
        return this.getName() + ":" + this.singularIndex + (this.hasPlural() ? "," + this.pluralIndex : "");
    }
}
