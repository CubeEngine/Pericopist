package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import de.cubeisland.maven.plugins.messagecatalog.exception.ConfigurationException;
import de.cubeisland.maven.plugins.messagecatalog.exception.ConfigurationNotFoundException;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.exception.UnknownCatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.format.gettext.PlaintextGettextCatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceConfiguration;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.exception.UnknownSourceLanguageException;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.JavaSourceParser;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class MessageCatalogFactory
{
    private Map<String, Class<? extends SourceParser>> sourceParserMap;
    private Map<String, Class<? extends CatalogFormat>> catalogFormatMap;

    private Logger logger;

    public MessageCatalogFactory()
    {
        this(Logger.getLogger("messagecatalog"));
    }

    public MessageCatalogFactory(Logger logger)
    {
        this.logger = logger;
        this.sourceParserMap = new HashMap<String, Class<? extends SourceParser>>();
        this.catalogFormatMap = new HashMap<String, Class<? extends CatalogFormat>>();

        this.loadDefaultClasses();
    }

    public Class<? extends SourceParser> getSourceParser(String language)
    {
        return this.sourceParserMap.get(language);
    }

    public Class<? extends CatalogFormat> getCatalogFormat(String format)
    {
        return this.catalogFormatMap.get(format);
    }

    public MessageCatalog getMessageCatalog(File configuration, Context context) throws ConfigurationException
    {
        if(!configuration.exists())
        {
            throw new ConfigurationNotFoundException("The configuration file does not exist!");
        }

        VelocityEngine velocityEngine = Misc.getVelocityEngine(configuration);
        velocityEngine.init();

        Template configTemplate = velocityEngine.getTemplate(configuration.getName());
        StringWriter stringWriter = new StringWriter();

        configTemplate.merge(context, stringWriter);

        SourceParser sourceParser = null;
        Node sourceNode = null;

        CatalogFormat catalogFormat = null;
        Node catalogNode = null;

        NodeList list = this.getRootNode(stringWriter.toString()).getChildNodes();
        for(int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if(node.getNodeName().equals("source"))
            {
                String language = node.getAttributes().getNamedItem("language").getTextContent();
                Class<? extends SourceParser> sourceParserClass = this.getSourceParser(language);
                if(sourceParserClass == null)
                {
                    throw new UnknownSourceLanguageException("Unknown source language " + language);
                }
                try
                {
                    sourceParserClass.getConstructor(Logger.class).newInstance(this.logger);
                    sourceParser = sourceParserClass.getConstructor(Logger.class).newInstance(this.logger);
                }
                catch (Exception e)
                {
                    throw new ConfigurationException("Could not create a SourceParser instance of " + sourceParserClass.getName());
                }
                sourceNode = node;
            }
            else if(node.getNodeName().equals("catalog"))
            {
                String format = node.getAttributes().getNamedItem("format").getTextContent();
                Class<? extends CatalogFormat> catalogFormatClass = this.getCatalogFormat(format);
                if(catalogFormatClass == null)
                {
                    throw new UnknownCatalogFormatException("Unknown catalog format " + format);
                }
                try
                {
                    catalogFormat = catalogFormatClass.getConstructor(Logger.class).newInstance(this.logger);
                }
                catch (Exception e)
                {
                    throw new ConfigurationException("Could not create an CatalogFormat instance of " + catalogFormatClass.getName());
                }
                catalogNode = node;
            }
        }

        try
        {
            Class<? extends SourceConfiguration> sourceConfigurationClass = sourceParser.getConfigClass();
            Class<? extends CatalogConfiguration> catalogConfigurationClass = catalogFormat.getConfigClass();

            JAXBContext jaxbContext = JAXBContext.newInstance(sourceConfigurationClass, catalogConfigurationClass);

            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            SourceConfiguration sourceConfiguration = sourceConfigurationClass.cast(unmarshaller.unmarshal(sourceNode));
            CatalogConfiguration catalogConfiguration = catalogConfigurationClass.cast(unmarshaller.unmarshal(catalogNode));

            return new MessageCatalog(sourceParser, sourceConfiguration, catalogFormat, catalogConfiguration, context, this.logger);

        }
        catch (JAXBException e)
        {
            throw new ConfigurationException(e);
        }
    }

    private Node getRootNode(String xml) throws ConfigurationException
    {
        Document document = null;
        try
        {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xml));
            document = builder.parse(inputSource);
        }
        catch (Exception e)
        {
            throw new ConfigurationException("Could not parse the configuration file", e);
        }
        assert document != null;

        NodeList list = document.getElementsByTagName("messagecatalog");
        if(list.getLength() == 0)
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
        this.sourceParserMap.put("java", JavaSourceParser.class);
        this.catalogFormatMap.put("gettext", PlaintextGettextCatalogFormat.class);
    }
}
