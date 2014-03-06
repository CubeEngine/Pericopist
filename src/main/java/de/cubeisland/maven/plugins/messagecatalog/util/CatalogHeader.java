package de.cubeisland.maven.plugins.messagecatalog.util;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CatalogHeader
{
    private List<String> comments;

    public CatalogHeader(String headerResource, Context context) throws IOException
    {
        URL headerUrl = Misc.getResource(headerResource);
        if (headerUrl == null)
        {
            throw new FileNotFoundException("The header resource '" + headerResource + "' was not found in file system or as URL.");
        }

        this.comments = new LinkedList<String>();

        VelocityEngine engine = new VelocityEngine();
        engine.init();

        StringWriter stringWriter = new StringWriter();
        engine.evaluate(context, stringWriter, "catalogheader", Misc.getContent(headerUrl));

        Collections.addAll(this.comments, stringWriter.toString().split("\n"));
    }

    public Collection<String> getComments()
    {
        return this.comments;
    }
}
