package de.cubeisland.maven.plugins.messageextractor;

import org.apache.velocity.tools.ToolManager;

import de.cubeisland.maven.plugins.messageextractor.exception.MessageCatalogException;

public class Test
{
    public static void main(String[] args)
    {
        ToolManager toolManager = new ToolManager(true);

        MessageCatalogFactory factory = new MessageCatalogFactory();
        MessageCatalog messageCatalog;
        try
        {
            messageCatalog = factory.getMessageCatalog("./example.xml", toolManager.createContext());
            //            messageCatalog = factory.getMessageCatalog("https://raw.github.com/CubeEngineDev/messageextractor-maven-plugin/reconstruction/example.xml", toolManager.createContext());
            messageCatalog.generateCatalog();
        }
        catch (MessageCatalogException e)
        {
            e.printStackTrace();
        }
    }
}
