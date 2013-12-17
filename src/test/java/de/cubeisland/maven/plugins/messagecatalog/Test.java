package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.tools.ToolManager;

import java.io.File;

import de.cubeisland.maven.plugins.messagecatalog.exception.ConfigurationException;

public class Test
{
    public static void main(String[] args)
    {
        ToolManager toolManager = new ToolManager(true);

        MessageCatalogFactory factory = new MessageCatalogFactory();
        MessageCatalog messageCatalog;
        try
        {
            messageCatalog = factory.getMessageCatalog(new File("./example.xml"), toolManager.createContext());
            messageCatalog.generateCatalog();
        }
        catch (ConfigurationException e)
        {
            e.printStackTrace();
        }
    }
}
