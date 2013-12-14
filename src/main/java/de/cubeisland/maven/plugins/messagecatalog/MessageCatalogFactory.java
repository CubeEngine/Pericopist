package de.cubeisland.maven.plugins.messagecatalog;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.format.UnknownCatalogFormatException;
import de.cubeisland.maven.plugins.messagecatalog.format.gettext.PlaintextGettextCatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.UnknownSourceLanguageException;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.JavaSourceParser;

public class MessageCatalogFactory
{
    private Map<String, Class<? extends SourceParser>> sourceParserMap;
    private Map<String, Class<? extends CatalogFormat>> catalogFormatMap;

    private Logger logger;

    private SourceParser sourceParser;
    private CatalogFormat catalogFormat;

    public MessageCatalogFactory(File configuration) throws ParserConfigurationException, SAXException, IOException, UnknownSourceLanguageException, InstantiationException, UnknownCatalogFormatException, IllegalAccessException
    {
        this(configuration, null, Logger.getLogger("messagecatalog"));
    }

    public MessageCatalogFactory(File configuration, Logger logger) throws ParserConfigurationException, SAXException, IOException, UnknownSourceLanguageException, InstantiationException, UnknownCatalogFormatException, IllegalAccessException
    {
        this(configuration, null, logger);
    }

    public MessageCatalogFactory(File configuration, Context velocityContext, Logger logger) throws IOException, SAXException, ParserConfigurationException, UnknownSourceLanguageException, InstantiationException, UnknownCatalogFormatException, IllegalAccessException
    {
        this.logger = logger;
        this.sourceParserMap = new HashMap<String, Class<? extends SourceParser>>();
        this.catalogFormatMap = new HashMap<String, Class<? extends CatalogFormat>>();

        this.loadDefaultClasses();

        VelocityEngine velocityEngine = this.getVeloctiyEngine(configuration);
        velocityEngine.init();

        Template configTemplate = velocityEngine.getTemplate(configuration.getName());
        StringWriter stringWriter = new StringWriter();

        configTemplate.merge(velocityContext, stringWriter);

        this.parseConfiguration(stringWriter.toString());

        // TODO parse Config with velocity(!) and load source parser and catalog!
    }

    private void loadDefaultClasses()
    {
        this.sourceParserMap.put("java", JavaSourceParser.class);
        this.catalogFormatMap.put("gettext", PlaintextGettextCatalogFormat.class);
    }

    public Class<? extends SourceParser> getSourceParserClass(String language)
    {
        return this.sourceParserMap.get(language);
    }

    public Class<? extends CatalogFormat> getCatalogFormatClass(String format)
    {
        return this.catalogFormatMap.get(format);
    }

    private VelocityEngine getVeloctiyEngine(File file)
    {
        Properties properties = new Properties();
        properties.put("resource.loader", "file");
        properties.put("file.resource.loader.class", FileResourceLoader.class.getName());
        properties.put("file.resource.loader.description", "Velocity File Resource Loader");
        properties.put("file.resource.loader.path", file.getParentFile().getAbsolutePath());
        properties.put("file.resource.loader.cache", false);

        return new VelocityEngine(properties);
    }

    private void parseConfiguration(String xml) throws ParserConfigurationException, IOException, SAXException, UnknownSourceLanguageException, IllegalAccessException, InstantiationException, UnknownCatalogFormatException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        InputSource inputSource = new InputSource(new StringReader(xml));
        Document document = builder.parse(inputSource);

        NodeList list = document.getElementsByTagName("messagecatalog");
        Node node = list.item(0);

        list = node.getChildNodes();
        for(int i = 0; i < list.getLength(); i++)
        {
            node = list.item(0);
            if(node.getNodeName().equals("source"))
            {
                String language = node.getAttributes().getNamedItem("language").getTextContent();
                Class<? extends SourceParser> sourceParser = this.getSourceParserClass(language);
                if(sourceParser == null)
                {
                    throw new UnknownSourceLanguageException("Unknown source language " + language);
                }
                this.sourceParser = sourceParser.newInstance();
                this.sourceParser.init(this.logger);
            }
            else if(node.getNodeName().equals("catalog"))
            {
                String format = node.getAttributes().getNamedItem("format").getTextContent();
                Class<? extends CatalogFormat> catalogFormat = this.getCatalogFormatClass(null);
                if(catalogFormat == null)
                {
                    throw new UnknownCatalogFormatException("Unknown catalog format " + format);
                }
                this.catalogFormat = catalogFormat.newInstance();
                this.catalogFormat.init(this.logger);
            }
            else
            {
                this.logger.info("Unknown node " + node.getNodeName());
            }
        }
    }
}
