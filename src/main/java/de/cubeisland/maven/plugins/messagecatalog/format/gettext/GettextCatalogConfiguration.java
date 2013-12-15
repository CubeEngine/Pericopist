package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;

import de.cubeisland.maven.plugins.messagecatalog.exception.MissingNodeException;
import de.cubeisland.maven.plugins.messagecatalog.format.AbstractCatalogConfiguration;

public class GettextCatalogConfiguration extends AbstractCatalogConfiguration
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

    public void parse(Node node) throws MissingNodeException
    {
        System.out.println("found catalog config " + this.getClass().getName());

        NodeList nodeList = node.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++)
        {
            Node subNode = nodeList.item(i);
            String nodeName = subNode.getNodeName();

            if(nodeName.equals("template"))
            {
                this.templateFile = new File(subNode.getTextContent().trim());
            }
            else if(nodeName.equals("removeUnusedMessages"))
            {
                this.removeUnusedMessages = Boolean.parseBoolean(subNode.getTextContent());
            }
            else if(nodeName.equals("header"))
            {
                this.header = new File(subNode.getTextContent().trim());
            }
        }

        if(this.templateFile == null)
        {
            throw MissingNodeException.of(this, "template");
        }
    }

    public String getFormat()
    {
        return "gettext";
    }
}
