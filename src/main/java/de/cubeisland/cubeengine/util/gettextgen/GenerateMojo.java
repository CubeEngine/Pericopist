package de.cubeisland.cubeengine.util.gettextgen;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import de.cubeisland.cubeengine.util.gettextgen.format.CatalogFormat;
import de.cubeisland.cubeengine.util.gettextgen.format.CatalogFormatFactory;
import de.cubeisland.cubeengine.util.gettextgen.parser.SourceParser;
import de.cubeisland.cubeengine.util.gettextgen.parser.SourceParserFactory;
import de.cubeisland.cubeengine.util.gettextgen.parser.TranslatableMessage;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Blabla
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMojo
{
    /**
     * @parameter
     */
    public File sourcePath = new File(".");

    /**
     * @parameter
     */
    public String templateFile = "src/main/resources/en_US";

    /**
     * @parameter
     */
    public String language = "java";

    /**
     * @parameter
     */
    public String outputFormat = "gettext";

    /**
     * @parameter
     */
    public Map<String, Object> config = Collections.emptyMap();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {

        this.getLog().error("source path: " + this.sourcePath.getAbsolutePath());

        SourceParser parser = SourceParserFactory.newSourceParser(this.language, this.config, this.getLog());
        Set<TranslatableMessage> messages = parser.parse (this.sourcePath);

        CatalogFormat catalogFormat = CatalogFormatFactory.newCatalogFormat(this.outputFormat, config, this.getLog());
        File file = new File(this.templateFile + "." + catalogFormat.getFileExtension());
        try
        {
            catalogFormat.write(file, messages);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException("Failed to write the message catalog.", e);
        }
    }
}
