package de.cubeisland.maven.plugins.messagecatalog.parser.java.config;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.exception.MissingNodeException;
import de.cubeisland.maven.plugins.messagecatalog.parser.AbstractSourceConfiguration;

public class JavaSourceConfiguration extends AbstractSourceConfiguration
{
    private Set<TranslatableMethod> translatableMethods = new HashSet<TranslatableMethod>();
    private Set<TranslatableAnnotation> translatableAnnotations = new HashSet<TranslatableAnnotation>();

    public Collection<TranslatableMethod> getTranslatableMethods()
    {
        return translatableMethods;
    }

    public Collection<TranslatableAnnotation> getTranslatableAnnotations()
    {
        return translatableAnnotations;
    }

    public void parse(Node node) throws MissingNodeException
    {
        System.out.println("found source config " + this.getClass().getName());

        NodeList nodeList = node.getChildNodes();
        for(int i = 0; i < nodeList.getLength(); i++)
        {
            Node subNode = nodeList.item(i);
            String nodeName = subNode.getNodeName();

            if(nodeName.equals("directory"))
            {
                this.directory = new File(subNode.getTextContent().trim());
            }
            else if(nodeName.equals("methods"))
            {
                NodeList methodList = subNode.getChildNodes();
                for(int j = 0; j < methodList.getLength(); j++)
                {
                    subNode = methodList.item(j);
                    if(subNode.getNodeName().equals("method"))
                    {
                        this.translatableMethods.add(new TranslatableMethod(subNode.getTextContent().trim()));
                    }
                }
            }
            else if(nodeName.equals("annotations"))
            {
                NodeList annotationList = subNode.getChildNodes();
                for(int j = 0; j < annotationList.getLength(); j++)
                {
                    subNode = annotationList.item(j);
                    if(subNode.getNodeName().equals("annotation"))
                    {
                        this.translatableAnnotations.add(new TranslatableAnnotation(subNode.getTextContent().trim()));
                    }
                }
            }
        }

        if(this.directory == null)
        {
            throw MissingNodeException.of(this, "directory");
        }
    }

    public String getLanguage()
    {
        return "java";
    }

    public TranslatableMethod getMethod(String name)
    {
        for (TranslatableMethod method : this.translatableMethods)
        {
            if (method.getName().equals(name))
            {
                return method;
            }
        }
        return null;
    }

    public TranslatableAnnotation getAnnotation(String name)
    {
        for (TranslatableAnnotation annotation : this.translatableAnnotations)
        {
            if (annotation.getFullQualifiedName().equals(name))
            {
                return annotation;
            }
        }
        return null;
    }
}
