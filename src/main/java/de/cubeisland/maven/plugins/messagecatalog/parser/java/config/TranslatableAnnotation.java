package de.cubeisland.maven.plugins.messagecatalog.parser.java.config;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "annotation")
public class TranslatableAnnotation
{
    @XmlElement(name = "name", required = true)
    private String fqn;

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private Set<String> fields = new HashSet<String>()
    {
        {
            this.add("value");
        }
    };

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
        for (String field : this.fields)
        {
            if (field.equals(name))
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
        if (fieldAmount != 0)
        {
            builder.append(":");
            for (String field : this.getFields())
            {
                builder.append(field);
                fieldAmount--;
                if (fieldAmount != 0)
                {
                    builder.append(",");
                }
            }
        }
        return builder.toString();
    }
}
