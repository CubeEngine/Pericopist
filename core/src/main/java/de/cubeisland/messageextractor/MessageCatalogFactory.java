/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Phillip Schichtel, Stefan Wolf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.cubeisland.messageextractor;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.SystemLogChute;
import org.apache.velocity.tools.ToolManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.exception.ConfigurationNotFoundException;
import de.cubeisland.messageextractor.exception.MessageCatalogException;
import de.cubeisland.messageextractor.exception.UnknownCatalogFormatException;
import de.cubeisland.messageextractor.exception.UnknownSourceLanguageException;
import de.cubeisland.messageextractor.extractor.ExtractorConfiguration;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.format.CatalogConfiguration;
import de.cubeisland.messageextractor.format.gettext.GettextCatalogConfiguration;
import de.cubeisland.messageextractor.util.Misc;

/**
 * This class is a little helper. It helps to create a MessageCatalog instance which is needed to
 * create the catalog. With this class one can set the configuration up with an xml file.
 *
 * @see de.cubeisland.messageextractor.MessageCatalog
 * @see #getMessageCatalog(String, java.nio.charset.Charset, org.apache.velocity.context.Context, java.util.logging.Logger)
 */
public class MessageCatalogFactory
{
    private Map<String, Class<? extends ExtractorConfiguration>> extractorConfigurationMap;
    private Map<String, Class<? extends CatalogConfiguration>> catalogConfigurationMap;

    private final DocumentBuilderFactory documentBuilderFactory;

    /**
     * creates a new instance of this class.
     */
    public MessageCatalogFactory()
    {
        this.extractorConfigurationMap = new HashMap<>();
        this.catalogConfigurationMap = new HashMap<>();

        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();

        this.loadDefaultClasses();
    }

    /**
     * This method adds a new extractor configuration.
     *
     * @param language the language which is related to the ExtractorConfiguration
     * @param config   the ExtractorConfiguration class
     *
     * @return the previous ExtractorConfiguration class related to the language
     */
    public Class<? extends ExtractorConfiguration> addExtractorConfiguration(String language, Class<? extends ExtractorConfiguration> config)
    {
        return this.extractorConfigurationMap.put(language, config);
    }

    /**
     * This method adds a new catalog configuration.
     *
     * @param format the catalog name which is related to the CatalogConfiguration
     * @param config the CatalogConfiguration class
     *
     * @return the previous CatalogConfiguration class related to the catalog name
     */
    public Class<? extends CatalogConfiguration> addCatalogConfiguration(String format, Class<? extends CatalogConfiguration> config)
    {
        return this.catalogConfigurationMap.put(format, config);
    }

    /**
     * This method returns the ExtractorConfiguration of the specified language
     *
     * @param language the language of the ExtractorConfiguration
     *
     * @return ExtractorConfiguration instance
     */
    private Class<? extends ExtractorConfiguration> getExtractorConfigurationClass(String language)
    {
        return this.extractorConfigurationMap.get(language);
    }

    /**
     * This method returns the CatalogConfiguration instance of the specified format
     *
     * @param format the format of the CatalogConfiguration
     *
     * @return CatalogConfiguration instance
     */
    private Class<? extends CatalogConfiguration> getCatalogConfigurationClass(String format)
    {
        return this.catalogConfigurationMap.get(format);
    }

    /**
     * This method creates a MessageCatalog instance.
     *
     * @param resource the xml configuration resource
     * @param charset  the default charset
     *
     * @return MessageCatalog instance
     *
     * @throws MessageCatalogException if an error occurs
     * @see #getMessageCatalog(String, java.nio.charset.Charset, org.apache.velocity.context.Context, java.util.logging.Logger)
     */
    public MessageCatalog getMessageCatalog(String resource, Charset charset) throws MessageCatalogException
    {
        return this.getMessageCatalog(resource, charset, (Context) null);
    }

    /**
     * This method creates a MessageCatalog instance.
     *
     * @param resource the xml configuration resource
     * @param charset  the default charset
     * @param logger   a logger
     *
     * @return MessageCatalog instance
     *
     * @throws MessageCatalogException if an error occurs
     * @see #getMessageCatalog(String, java.nio.charset.Charset, org.apache.velocity.context.Context, java.util.logging.Logger)
     */
    public MessageCatalog getMessageCatalog(String resource, Charset charset, Logger logger) throws MessageCatalogException
    {
        return this.getMessageCatalog(resource, charset, null, logger);
    }

    /**
     * This method creates a MessageCatalog instance.
     *
     * @param resource        the xml configuration resource
     * @param charset         the default charset
     * @param velocityContext a velocity context which is used to evaluate the configuration
     *
     * @return MessageCatalog instance
     *
     * @throws MessageCatalogException if an error occurs
     * @see #getMessageCatalog(String, java.nio.charset.Charset, org.apache.velocity.context.Context, java.util.logging.Logger)
     */
    public MessageCatalog getMessageCatalog(String resource, Charset charset, Context velocityContext) throws MessageCatalogException
    {
        return this.getMessageCatalog(resource, charset, velocityContext, null);
    }

    /**
     * This method creates a MessageCatalog instance. The configurations is specified with the help of an
     * xml file.
     * <p/>
     * Example:
     * <p/>
     * <pre>
     * {@code
     * <?xml version="1.0" encoding="UTF-8"?>
     * <extractor charset="utf-8">
     *     <source language="LANGUAGE">
     *         ...
     *     </source>
     *     <catalog format="FORMAT">
     *          ...
     *     </catalog>
     * </extractor>
     * }
     * </pre>
     * <p/>
     * The inner source tags are related to the language name. The language name is the name
     * specified with the method {@link #addExtractorConfiguration(String, Class)}.
     * A default language name is 'java' which links to the
     * {@link de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration}.
     * Have a look at this class to get a deeper knowledge about the xml file.
     * <p/>
     * the inner catalog tags are related to the format name. The format name is the name
     * specified with the method {@link #addCatalogConfiguration(String, Class)}.
     * A default format name is 'gettext' which links to the
     * {@link de.cubeisland.messageextractor.format.gettext.GettextCatalogConfiguration}.
     * Have a look at this class to get a deeper knowledge about the xml file.
     *
     * @param resource        the xml configuration resource
     * @param charset         the default charset
     * @param velocityContext a velocity context which is used to evaluate the configuration
     * @param logger          a logger
     *
     * @return MessageCatalog instance
     *
     * @throws MessageCatalogException if an error occurs
     */
    public MessageCatalog getMessageCatalog(String resource, Charset charset, Context velocityContext, Logger logger) throws MessageCatalogException
    {
        URL configurationUrl = Misc.getResource(resource);
        if (configurationUrl == null)
        {
            throw new ConfigurationNotFoundException("The configuration resource '" + resource + "' was not found in file system or as URL.");
        }

        if (velocityContext == null)
        {
            velocityContext = new ToolManager(false).createContext();
        }

        Charset defaultCharset = charset;
        Node sourceNode = null;
        Node catalogNode = null;

        Node rootNode = this.getRootNode(this.loadConfiguration(configurationUrl, velocityContext, charset));
        Node charsetNode = rootNode.getAttributes().getNamedItem("charset");
        if (charsetNode != null)
        {
            defaultCharset = Charset.forName(charsetNode.getTextContent());
        }

        NodeList list = rootNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++)
        {
            Node node = list.item(i);
            if ("source".equals(node.getNodeName()))
            {
                sourceNode = node;
            }
            else if ("catalog".equals(node.getNodeName()))
            {
                catalogNode = node;
            }
        }

        if (sourceNode == null)
        {
            throw new ConfigurationException("The configuration does not have a source tag");
        }
        if (catalogNode == null)
        {
            throw new ConfigurationException("The configuration does not have a catalog tag");
        }

        Node sourceLanguageNode = sourceNode.getAttributes().getNamedItem("language");
        if (sourceLanguageNode == null)
        {
            throw new UnknownSourceLanguageException("You must specify a language attribute to the source tag");
        }
        Class<? extends ExtractorConfiguration> extractorConfigurationClass = this.getExtractorConfigurationClass(sourceLanguageNode.getTextContent());
        if (extractorConfigurationClass == null)
        {
            throw new UnknownSourceLanguageException("Unknown source language " + sourceLanguageNode.getTextContent());
        }

        Node catalogFormatNode = catalogNode.getAttributes().getNamedItem("format");
        if (catalogFormatNode == null)
        {
            throw new UnknownCatalogFormatException("You must specify a format attribute to the catalog tag");
        }
        Class<? extends CatalogConfiguration> catalogConfigurationClass = this.getCatalogConfigurationClass(catalogFormatNode.getTextContent());
        if (catalogConfigurationClass == null)
        {
            throw new UnknownCatalogFormatException("Unknown catalog format " + catalogFormatNode.getTextContent());
        }

        return this.createMessageCatalog(extractorConfigurationClass, sourceNode, catalogConfigurationClass, catalogNode, defaultCharset, logger);
    }

    /**
     * This method creates a MessageCatalog instance. It uses the JAXB XML serializer to deserialize the extractor and catalog nodes.
     *
     * @param extractorConfigurationClass the class of the ExtractorConfiguration
     * @param sourceNode                  the node which describes the ExtractorConfiguration
     * @param catalogConfigurationClass   the class of the CatalogFormat
     * @param catalogNode                 the node which describes the CatalogFormat
     * @param charset                     The charset which shall be used for the configurations
     * @param logger                      the logger for the message catalog
     *
     * @return a message catalog instance
     *
     * @throws MessageCatalogException if a JAXB exception occurs or the message catalog can't be created
     */
    private MessageCatalog createMessageCatalog(Class<? extends ExtractorConfiguration> extractorConfigurationClass, Node sourceNode, Class<? extends CatalogConfiguration> catalogConfigurationClass, Node catalogNode, Charset charset, Logger logger) throws MessageCatalogException
    {
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(extractorConfigurationClass, catalogConfigurationClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            ExtractorConfiguration extractorConfiguration = extractorConfigurationClass.cast(unmarshaller.unmarshal(sourceNode));
            CatalogConfiguration catalogConfiguration = catalogConfigurationClass.cast(unmarshaller.unmarshal(catalogNode));

            if (extractorConfiguration.getCharset() == null)
            {
                extractorConfiguration.setCharset(charset);
            }
            if (catalogConfiguration.getCharset() == null)
            {
                catalogConfiguration.setCharset(charset);
            }

            return new MessageCatalog(extractorConfiguration, catalogConfiguration, logger);
        }
        catch (JAXBException e)
        {
            throw new ConfigurationException("The configuration file could not be parsed", e);
        }
    }

    /**
     * This method loads the configuration and evaluates it with the specified context
     *
     * @param resource the resource of the configuration
     * @param context  the velocity context
     * @param charset  the charset of the configuration
     *
     * @return the configuration
     *
     * @throws ConfigurationException if the resource couldn't be read
     */
    private String loadConfiguration(URL resource, Context context, Charset charset) throws ConfigurationException
    {
        // creates velocity engine with log properties and initialises it
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, SystemLogChute.class.getName());
        velocityEngine.setProperty(SystemLogChute.RUNTIME_LOG_LEVEL_KEY, "info");
        velocityEngine.setProperty(SystemLogChute.RUNTIME_LOG_SYSTEM_ERR_LEVEL_KEY, "warn");
        velocityEngine.init();

        // reads the configuration file
        String configuration;
        try
        {
            configuration = Misc.getContent(resource, charset);
        }
        catch (IOException e)
        {
            throw new ConfigurationException("The configuration file could not be read.", e);
        }

        // evaluates the configuration with the specified context as long as the new
        // configuration is the same as before or an error occurs
        String oldConfiguration;
        boolean success;
        do
        {
            oldConfiguration = configuration;

            StringWriter stringWriter = new StringWriter();
            success = velocityEngine.evaluate(context, stringWriter, "catalog_header_comments", configuration);

            configuration = stringWriter.toString();
        }
        while (!oldConfiguration.equals(configuration) && success);

        return configuration;
    }

    /**
     * This method returns the extractor node of the specified xml string
     *
     * @param xml xml string
     *
     * @return extractor root node
     *
     * @throws ConfigurationException if an error occurs or the node doesn't exist exactly one time
     */
    private Node getRootNode(String xml) throws ConfigurationException
    {
        Document document;
        try
        {
            document = this.documentBuilderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
        }
        catch (ParserConfigurationException | SAXException e)
        {
            throw new ConfigurationException("Could not extract the configuration file", e);
        }
        catch (IOException e)
        {
            throw new ConfigurationException("Could not read the configuration file", e);
        }

        NodeList list = document.getElementsByTagName("extractor");
        if (list.getLength() == 0)
        {
            throw new ConfigurationException("The configuration file doesn't have a <extractor> node");
        }
        else if (list.getLength() > 1)
        {
            throw new ConfigurationException("The configuration file has more than 1 <extractor> node");
        }
        return list.item(0);
    }

    /**
     * This method fills the instance of this class with default classes for the message
     * extractor and the catalog format
     */
    private void loadDefaultClasses()
    {
        this.addExtractorConfiguration("java", JavaExtractorConfiguration.class);
        this.addCatalogConfiguration("gettext", GettextCatalogConfiguration.class);
    }
}
