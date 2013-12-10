package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.util.HashMap;

import de.cubeisland.maven.plugins.messagecatalog.mojo.AbstractMessageCatalogMojo;
import de.cubeisland.maven.plugins.messagecatalog.mojo.GenerateMojo;

public class Test
{
    public static void main(String[] args)
    {
        AbstractMessageCatalogMojo mojo = new GenerateMojo();
        mojo.sourcePath = new File("src/test/resources");
        mojo.templateFile = "src/test/messages";


        mojo.options = new HashMap<String, Object>(2);
        mojo.options.put("methods", "getTranslation sendTranslated:1 _ getTranslationN:1,2 sendTranslationN:1,2");
        mojo.options.put("annotations", "test.anot.TestNormalAnnotation:desc,usage test.anot.TestSingleMemberAnnotation");


        try
        {
            mojo.execute();
        }
        catch (MojoExecutionException e)
        {
            e.printStackTrace();
        }
        catch (MojoFailureException e)
        {
            e.printStackTrace();
        }
    }
}
