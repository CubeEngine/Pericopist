package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import org.w3c.dom.Node;

import java.io.File;

import de.cubeisland.maven.plugins.messagecatalog.format.AbstractCatalogConfig;

public class GettextCatalogConfig extends AbstractCatalogConfig
{
    private File header;

    public File getHeader()
    {
        return header;
    }

    public void setHeader(File header)
    {
        this.header = header;
    }

    public void parse(Node node)
    {

    }

    public String getFormat()
    {
        return "gettext";
    }

    //    public void createVelocityContext()
//    {
//        ToolManager toolManager = new ToolManager();
//        this.velocityContext = toolManager.createContext();
//    }
}
