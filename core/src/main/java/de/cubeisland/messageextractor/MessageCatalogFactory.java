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

public class MessageCatalogFactory
{
    private Map<String, Class<? extends ExtractorConfiguration>> extractorConfigurationMap;
    private Map<String, Class<? extends CatalogConfiguration>> catalogConfigurationMap;

    private final DocumentBuilderFactory documentBuilderFactory;

    public MessageCatalogFactory()
    {
        this.extractorConfigurationMap = new HashMap<String, Class<? extends ExtractorConfiguration>>();
        this.catalogConfigurationMap = new HashMap<String, Class<? extends CatalogConfiguration>>();

        this.documentBuilderFactory = DocumentBuilderFactory.newInstance();

        this.loadDefaultClasses();
    }

    private Class<? extends ExtractorConfiguration> getExtractorConfigurationClass(String language)
    {
        return this.extractorConfigurationMap.get(language);
    }

    private Class<? extends CatalogConfiguration> getCatalogConfigurationClass(String format)
    {
        return this.catalogConfigurationMap.get(format);
    }

    public MessageCatalog getMessageCatalog(String resource, Charset charset) throws MessageCatalogException
    {
        return this.getMessageCatalog(resource, charset, (Context) null);
    }

    public MessageCatalog getMessageCatalog(String resource, Charset charset, Logger logger) throws MessageCatalogException
    {
        return this.getMessageCatalog(resource, charset, null, logger);
    }

    public MessageCatalog getMessageCatalog(String resource, Charset charset, Context veloctiyContext) throws MessageCatalogException
    {
        return this.getMessageCatalog(resource, charset, veloctiyContext, null);
    }

    public MessageCatalog getMessageCatalog(String resource, Charset charset, Context veloctiyContext, Logger logger) throws MessageCatalogException
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
            velocityEngine.evaluate(veloctiyContext, stringWriter, "configuration", Misc.getContent(configurationUrl, charset));
        }
        catch (IOException e)
        {
            throw new ConfigurationException("The configuration file could not be read.", e);
        }

        Charset defaultCharset = charset;
        Node sourceNode = null;
        Node catalogNode = null;

        Node rootNode = this.getRootNode(stringWriter.toString());
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

        return this.createMessageCatalog(extractorConfigurationClass, sourceNode, catalogConfigurationClass, catalogNode, defaultCharset, veloctiyContext, logger);
    }

    private MessageCatalog createMessageCatalog(Class<? extends ExtractorConfiguration> extractorConfigurationClass, Node sourceNode, Class<? extends CatalogConfiguration> catalogConfigurationClass, Node catalogNode, Charset charset, Context velocityContext, Logger logger) throws MessageCatalogException
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

            MessageCatalog messageCatalog = new MessageCatalog(extractorConfiguration, catalogConfiguration, logger);
            messageCatalog.setVelocityContext(velocityContext);

            return messageCatalog;
        }
        catch (JAXBException e)
        {
            throw new ConfigurationException("The configuration file could not be parsed", e);
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
            throw new ConfigurationException("Could not extract the configuration file", e);
        }
        catch (SAXException e)
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

    private void loadDefaultClasses()
    {
        this.extractorConfigurationMap.put("java", JavaExtractorConfiguration.class);
        this.catalogConfigurationMap.put("gettext", GettextCatalogConfiguration.class);
    }
}
