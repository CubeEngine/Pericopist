package de.cubeisland.maven.plugins.messagecatalog.util;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CatalogHeader
{
    private List<String> comments;

    public CatalogHeader(File file, Context context) throws FileNotFoundException
    {
        if (!file.exists())
        {
            throw new FileNotFoundException("The header file on path '" + file.getAbsolutePath() + " does not exists!");
        }

        this.comments = new LinkedList<String>();

        VelocityEngine engine = Misc.getVelocityEngine(file);
        engine.init();

        Template template = engine.getTemplate(file.getName());

        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);

        for (String line : stringWriter.toString().split("\n"))
        {
            this.comments.add(line);
        }
    }

    public Collection<String> getComments()
    {
        return this.comments;
    }
}
