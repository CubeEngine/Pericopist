package de.cubeisland.maven.plugins.messagecatalog.config;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;

import java.io.File;

public class CatalogConfig
{
    private String templateFile = "src/main/resources/messages";
    private String outputFormat = "gettext";
    private boolean removeUnusedMessages = false;

    private File header;
    private boolean addDefaultTools = false;
    private Context velocityContext;

    public String getTemplateFile()
    {
        return templateFile;
    }

    public void setTemplateFile(String templateFile)
    {
        this.templateFile = templateFile;
    }

    public String getOutputFormat()
    {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat)
    {
        this.outputFormat = outputFormat;
    }

    public boolean getRemoveUnusedMessages()
    {
        return removeUnusedMessages;
    }

    public void setRemoveUnusedMessages(boolean removeUnusedMessages)
    {
        this.removeUnusedMessages = removeUnusedMessages;
    }

    public File getHeader()
    {
        return header;
    }

    public void setHeader(File header)
    {
        this.header = header;
    }

    public boolean getAddDefaultTools()
    {
        return addDefaultTools;
    }

    public void setAddDefaultTools(boolean addDefaultTools)
    {
        this.addDefaultTools = addDefaultTools;
    }

    public Context getVelocityContext()
    {
        if(this.velocityContext == null)
        {
            this.createVelocityContext();
        }
        return this.velocityContext;
    }

    public void createVelocityContext()
    {
        ToolManager toolManager = new ToolManager(this.addDefaultTools);
        this.velocityContext = toolManager.createContext();
    }
}
