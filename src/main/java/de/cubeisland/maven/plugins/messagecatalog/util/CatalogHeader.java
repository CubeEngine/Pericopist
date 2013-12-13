package de.cubeisland.maven.plugins.messagecatalog.util;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class CatalogHeader
{
    private List<String> comments;

    public CatalogHeader(File file, Context context) throws FileNotFoundException
    {
        this.comments = new LinkedList<String>();

        Properties properties = new Properties();
        properties.put("resource.loader", "file");
        properties.put("file.resource.loader.class", FileResourceLoader.class.getName());
        properties.put("file.resource.loader.description", "Velocity File Resource Loader");
        properties.put("file.resource.loader.path", file.getParentFile().getAbsolutePath());
        properties.put("file.resource.loader.cache", false);

        VelocityEngine engine = new VelocityEngine(properties);
        engine.init();

        if (!file.exists())
        {
            throw new FileNotFoundException("The header file on path '" + file.getAbsolutePath() + " does not exists!");
        }

        Template template = engine.getTemplate(file.getName());

        StringWriter stringWriter = new StringWriter();
        template.merge(context, stringWriter);

        for (String line : stringWriter.toString().split("\n"))
        {
            comments.add(line);
        }
    }

    public Collection<String> getComments()
    {
        return this.comments;
    }
}
