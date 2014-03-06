package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.cubeisland.maven.plugins.messagecatalog.exception.ConfigurationException;
import de.cubeisland.maven.plugins.messagecatalog.exception.ConfigurationNotFoundException;
import de.cubeisland.maven.plugins.messagecatalog.exception.UnknownCatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.exception.UnknownSourceLanguageException;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.format.gettext.PlaintextGettextCatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.parser.ExtractorConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.parser.MessageExtractor;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.JavaMessageExtractor;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class MessageCatalogFactory
{
    private Map<String, Class<? extends MessageExtractor>> sourceParserMap;
    private Map<String, Class<? extends CatalogFormat>> catalogFormatMap;

    private final DocumentBuilderFactory documentBuilderFactory;

    public MessageCatalogFactory()
    {
        this.sourceParserMap = new HashMap<String, Class<? extends MessageExtractor>>();
        this.catalogFormatMap = new HashMap<String, Class<? extends CatalogFormat>>();

        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();

        this.loadDefaultClasses();
    }

    private Class<? extends MessageExtractor> getSourceParser(String language)
    {
        return this.sourceParserMap.get(language);
    }

    private Class<? extends CatalogFormat> getCatalogFormat(String format)
    {
        return this.catalogFormatMap.get(format);
    }

    public MessageCatalog getMessageCatalog(String resource) throws ConfigurationException
    {
        return this.getMessageCatalog(resource, null);
    }

    public MessageCatalog getMessageCatalog(String resource, Context context) throws ConfigurationException
    {
        URL configurationUrl = Misc.getResource(resource);
        if (configurationUrl == null)
        {
            throw new ConfigurationNotFoundException("The configuration resource '" + resource + "' was not found in file system or as URL.");
        }

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        StringWriter stringWriter = new StringWriter();
        try
        {
            velocityEngine.evaluate(context, stringWriter, "configuration", Misc.getContent(configurationUrl));
        }
        catch (IOException e)
        {
            throw new ConfigurationException("The configuration file could not be read.");
        }

        MessageExtractor messageExtractor = null;
        Node sourceNode = null;

        CatalogFormat catalogFormat = null;
        Node catalogNode = null;

        NodeList list = this.getRootNode(stringWriter.toString()).getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if (node.getNodeName().equals("source"))
            {
                String language = node.getAttributes().getNamedItem("language").getTextContent();
                Class<? extends MessageExtractor> messageExtractorClass = this.getSourceParser(language);
                if (messageExtractorClass == null)
                {
                    throw new UnknownSourceLanguageException("Unknown source language " + language);
                }
                try
                {
                    messageExtractor = messageExtractorClass.newInstance();
                }
                catch (Exception e)
                {
                    throw new ConfigurationException("Could not create a MessageExtractor instance of " + messageExtractorClass.getName());
                }
                sourceNode = node;
            }
            else if (node.getNodeName().equals("catalog"))
            {
                String format = node.getAttributes().getNamedItem("format").getTextContent();
                Class<? extends CatalogFormat> catalogFormatClass = this.getCatalogFormat(format);
                if (catalogFormatClass == null)
                {
                    throw new UnknownCatalogFormatException("Unknown catalog format " + format);
                }
                try
                {
                    catalogFormat = catalogFormatClass.newInstance();
                }
                catch (Exception e)
                {
                    throw new ConfigurationException("Could not create an CatalogFormat instance of " + catalogFormatClass.getName());
                }
                catalogNode = node;
            }
        }

        if (messageExtractor == null)
        {
            throw new ConfigurationException("The configuration does not have a source tag");
        }
        if (catalogFormat == null)
        {
            throw new ConfigurationException("The configuration does not have a catalog tag");
        }

        try
        {
            Class<? extends ExtractorConfiguration> extractorConfigurationClass = messageExtractor.getConfigClass();
            Class<? extends CatalogConfiguration> catalogConfigurationClass = catalogFormat.getConfigClass();

            JAXBContext jaxbContext = JAXBContext.newInstance(extractorConfigurationClass, catalogConfigurationClass);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            ExtractorConfiguration extractorConfiguration = extractorConfigurationClass.cast(unmarshaller.unmarshal(sourceNode));
            CatalogConfiguration catalogConfiguration = catalogConfigurationClass.cast(unmarshaller.unmarshal(catalogNode));

            return new MessageCatalog(messageExtractor, extractorConfiguration, catalogFormat, catalogConfiguration, context);
        }
        catch (JAXBException e)
        {
            throw new ConfigurationException(e);
        }
    }

    private Node getRootNode(String xml) throws ConfigurationException
    {
        Document document;
        try
        {
            document = this.documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        }
        catch (ParserConfigurationException e)
        {
            throw new ConfigurationException("Could not parse the configuration file", e);
        }
        catch (SAXException e)
        {
            throw new ConfigurationException("Could not parse the configuration file", e);
        }
        catch (IOException e)
        {
            throw new ConfigurationException("Could not read the configuration file", e);
        }

        NodeList list = document.getElementsByTagName("messagecatalog");
        if (list.getLength() == 0)
        {
            throw new ConfigurationException("The configuration file doesn't have a <messagecatalog> node");
        }
        else if (list.getLength() > 1)
        {
            throw new ConfigurationException("The configuration file has more than 1 <messagecatalog> node");
        }
        return list.item(0);
    }

    private void loadDefaultClasses()
    {
        this.sourceParserMap.put("java", JavaMessageExtractor.class);
        this.catalogFormatMap.put("gettext", PlaintextGettextCatalogFormat.class);
    }
}
