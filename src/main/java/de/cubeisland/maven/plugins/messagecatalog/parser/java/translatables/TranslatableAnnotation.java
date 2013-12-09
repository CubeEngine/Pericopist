package de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables;

import java.util.HashSet;
import java.util.Set;

public class TranslatableAnnotation
{
    private final String fqn;
    private final Set<String> fields;

    public TranslatableAnnotation(String annotation)
    {
        String[] parts = annotation.split(":");

        this.fqn = parts[0];
        this.fields = new HashSet<String>();

        if(parts.length > 1)
        {
            String[] fieldNames = parts[1].split(",");

            for(String field : fieldNames)
            {
                this.fields.add(field);
            }
        }
        else
        {
            this.fields.add("value");   // default is a SingleValueAnnotation
        }
    }

    public String getFullQualifiedName()
    {
        return this.fqn;
    }

    public String getSimpleName()
    {
        int ind = this.fqn.lastIndexOf(".");
        return ind < 0 ? this.fqn : this.fqn.substring(ind + 1);
    }

    public Set<String> getFields()
    {
        return this.fields;
    }

    public boolean hasField(String name)
    {
        for(String field : this.fields)
        {
            if(field.equals(name))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder(this.fqn);
        int fieldAmount = this.getFields().size();
        if(fieldAmount != 0)
        {
            builder.append(":");
            for(String field : this.getFields())
            {
                builder.append(field);
                fieldAmount--;
                if(fieldAmount != 0)
                {
                    builder.append(",");
                }
            }
        }
        return builder.toString();
    }
}
