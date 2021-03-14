/*
 * The MIT License
 * Copyright © 2013 Cube Island
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
package org.cubeengine.pericopist;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolManager;
import org.cubeengine.pericopist.configuration.Mergeable;
import org.cubeengine.pericopist.configuration.MergeableArray;
import org.cubeengine.pericopist.exception.ConfigurationException;
import org.cubeengine.pericopist.exception.ConfigurationNotFoundException;
import org.cubeengine.pericopist.exception.PericopistException;
import org.cubeengine.pericopist.exception.UnknownCatalogFormatException;
import org.cubeengine.pericopist.exception.UnknownSourceLanguageException;
import org.cubeengine.pericopist.extractor.ExtractorConfiguration;
import org.cubeengine.pericopist.extractor.java.configuration.JavaExtractorConfiguration;
import org.cubeengine.pericopist.format.CatalogConfiguration;
import org.cubeengine.pericopist.format.gettext.GettextCatalogConfiguration;
import org.cubeengine.pericopist.util.Misc;
import org.cubeengine.pericopist.util.Pair;
import org.cubeengine.pericopist.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is a little helper. It helps to create a {@link Pericopist} instance which is needed to
 * create the catalog. With this class one can set the configuration up with an xml file.
 *
 * @see Pericopist
 * @see #getPericopist(String, java.nio.charset.Charset, int, org.apache.velocity.context.Context, java.util.logging.Logger)
 */
@SuppressWarnings("unused")
public class PericopistFactory
{
    private static final String CONFIGURATION_ROOT_TAG = "pericopist";
    private static final String RESOURCE_LOADER_CONTEXT_KEY = "resource";

    private final Map<String, Class<? extends ExtractorConfiguration>> extractorConfigurationMap;
    private final Map<String, Class<? extends CatalogConfiguration>> catalogConfigurationMap;

    private final DocumentBuilderFactory documentBuilderFactory;

    /**
     * creates a new instance of this class.
     */
    public PericopistFactory()
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
     * This method creates a {@link Pericopist} instance.
     *
     * @param resource the xml configuration resource
     *
     * @return {@link Pericopist} instance
     *
     * @throws PericopistException if an error occurs
     * @see #getPericopist(String, java.nio.charset.Charset, int, org.apache.velocity.context.Context, java.util.logging.Logger)
     */
    public Pericopist getPericopist(String resource) throws PericopistException
    {
        return this.getPericopist(resource, 5000, null, null);
    }

    /**
     * This method creates a {@link Pericopist} instance.
     *
     * @param resource        the xml configuration resource
     * @param readTimeout     read timeout of the configuration read operation in milliseconds
     * @param velocityContext a velocity context which is used to evaluate the configuration
     * @param logger          a logger
     *
     * @return {@link Pericopist} instance
     *
     * @throws PericopistException if an error occurs
     * @see #getPericopist(String, java.nio.charset.Charset, int, org.apache.velocity.context.Context, java.util.logging.Logger)
     */
    public Pericopist getPericopist(String resource, int readTimeout, Context velocityContext, Logger logger) throws PericopistException
    {
        return this.getPericopist(resource, StandardCharsets.UTF_8, readTimeout, velocityContext, logger);
    }

    /**
     * <p>
     * This method creates a {@link Pericopist} instance. The configurations is specified with the help of an
     * xml file.
     * </p>
     *
     * Example:
     *
     * <pre>
     * {@code
     * <?xml version="1.0" encoding="UTF-8"?>
     * <pericopist charset="utf-8" parent="path">
     *     <source language="LANGUAGE">
     *         ...
     *     </source>
     *     <catalog format="FORMAT">
     *          ...
     *     </catalog>
     * </pericopist>
     * }
     * </pre>
     *
     * <p>
     * The inner source tags are related to the language name. The language name is the name
     * specified with the method {@link #addExtractorConfiguration(String, Class)}.
     * A default language name is 'java' which links to the {@link JavaExtractorConfiguration}.
     * Have a look at this class to get a deeper knowledge about the xml file.
     * </p>
     *
     * <p>
     * the inner catalog tags are related to the format name. The format name is the name
     * specified with the method {@link #addCatalogConfiguration(String, Class)}.
     * A default format name is 'gettext' which links to the {@link GettextCatalogConfiguration}.
     * Have a look at this class to get a deeper knowledge about the xml file.
     * </p>
     *
     * @param resource        the xml configuration resource
     * @param charset         the default charset
     * @param readTimeout     read timeout of the configuration read operation in milliseconds
     * @param velocityContext a velocity context which is used to evaluate the configuration
     * @param logger          a logger
     *
     * @return {@link Pericopist} instance
     *
     * @throws PericopistException if an error occurs
     */
    public Pericopist getPericopist(String resource, Charset charset, int readTimeout, Context velocityContext, Logger logger) throws PericopistException
    {
        if (velocityContext == null)
        {
            velocityContext = new ToolManager(false).createContext();
        }
        if (!velocityContext.containsKey(RESOURCE_LOADER_CONTEXT_KEY))
        {
            velocityContext.put(RESOURCE_LOADER_CONTEXT_KEY, new ResourceLoader());
        }

        // creates velocity engine with log properties and initialises it
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        PericopistConfiguration extractorConfiguration = this.loadPericopistConfiguration(resource, charset, readTimeout, velocityEngine, velocityContext);

        if (extractorConfiguration == null)
        {
            throw new ConfigurationException("The configuration neither has a source tag nor a catalog tag.");
        }
        if (extractorConfiguration.extractorConfiguration == null)
        {
            throw new ConfigurationException("The configuration does not have a source tag.");
        }
        if (extractorConfiguration.catalogConfiguration == null)
        {
            throw new ConfigurationException("The configuration does not have a catalog tag.");
        }

        return new Pericopist(extractorConfiguration.extractorConfiguration, extractorConfiguration.catalogConfiguration, logger);
    }

    /**
     * This method loads the specified {@link Pericopist} configuration resources
     *
     * @param resource        the xml configuration resource
     * @param charset         the charset of the configuration
     * @param readTimeout     read timeout of the configuration read operation in milliseconds
     * @param velocityEngine  used velocity engine
     * @param velocityContext a velocity context which is used to evaluate the configuration
     *
     * @return {@link PericopistConfiguration} which stores the parsed configurations
     */
    private PericopistConfiguration loadPericopistConfiguration(String resource, Charset charset, int readTimeout, VelocityEngine velocityEngine, Context velocityContext) throws ConfigurationException
    {
        URL configurationUrl = Misc.getResource(resource);
        if (configurationUrl == null)
        {
            throw new ConfigurationNotFoundException("The configuration resource '" + resource + "' was not found in file system or as URL.");
        }

        PericopistConfiguration parent = null;
        Charset defaultCharset = charset;
        Node sourceNode = null;
        Node catalogNode = null;

        Pair<URL, String> urlConfigPair = this.loadConfiguration(configurationUrl, charset, readTimeout, velocityEngine, velocityContext);
        configurationUrl = urlConfigPair.getKey();
        Node rootNode = this.getRootNode(urlConfigPair.getValue());
        Node charsetNode = rootNode.getAttributes().getNamedItem("charset");
        if (charsetNode != null)
        {
            defaultCharset = Charset.forName(charsetNode.getTextContent());
        }

        Node parentNode = rootNode.getAttributes().getNamedItem("parent");
        if (parentNode != null)
        {
            String parentResource = Misc.resolvePath(configurationUrl.toExternalForm(), parentNode.getTextContent());
            parent = this.loadPericopistConfiguration(parentResource, charset, readTimeout, velocityEngine, velocityContext);
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

        List<Class<?>> jaxbClasses = new ArrayList<>();

        Class<? extends ExtractorConfiguration> extractorConfigurationClass = null;
        if (sourceNode != null)
        {
            Node sourceLanguageNode = sourceNode.getAttributes().getNamedItem("language");
            if (sourceLanguageNode == null)
            {
                throw new UnknownSourceLanguageException("You must specify a language attribute to the source tag");
            }
            extractorConfigurationClass = this.getExtractorConfigurationClass(sourceLanguageNode.getTextContent());
            if (extractorConfigurationClass == null)
            {
                throw new UnknownSourceLanguageException("Unknown source language " + sourceLanguageNode.getTextContent());
            }

            jaxbClasses.add(extractorConfigurationClass);
        }

        Class<? extends CatalogConfiguration> catalogConfigurationClass = null;
        if (catalogNode != null)
        {
            Node catalogFormatNode = catalogNode.getAttributes().getNamedItem("format");
            if (catalogFormatNode == null)
            {
                throw new UnknownCatalogFormatException("You must specify a format attribute to the catalog tag");
            }
            catalogConfigurationClass = this.getCatalogConfigurationClass(catalogFormatNode.getTextContent());
            if (catalogConfigurationClass == null)
            {
                throw new UnknownCatalogFormatException("Unknown catalog format " + catalogFormatNode.getTextContent());
            }

            jaxbClasses.add(catalogConfigurationClass);
        }

        if (jaxbClasses.isEmpty())
        {
            return parent;
        }

        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(jaxbClasses.toArray(new Class[jaxbClasses.size()]));
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            ExtractorConfiguration extractorConfiguration = null;
            if (sourceNode != null)
            {
                extractorConfiguration = extractorConfigurationClass.cast(unmarshaller.unmarshal(sourceNode));

                if (parent != null && parent.extractorConfiguration != null)
                {
                    this.mergeObjects(extractorConfiguration, parent.extractorConfiguration);
                }

                if (extractorConfiguration != null && extractorConfiguration.getCharset() == null)
                {
                    extractorConfiguration.setCharset(defaultCharset);
                }
            }
            else if (parent != null)
            {
                extractorConfiguration = parent.extractorConfiguration;
            }

            CatalogConfiguration catalogConfiguration = null;
            if (catalogNode != null)
            {
                catalogConfiguration = catalogConfigurationClass.cast(unmarshaller.unmarshal(catalogNode));

                if (parent != null && parent.catalogConfiguration != null)
                {
                    this.mergeObjects(catalogConfiguration, parent.catalogConfiguration);
                }

                if (catalogConfiguration != null && catalogConfiguration.getCharset() == null)
                {
                    catalogConfiguration.setCharset(defaultCharset);
                }
            }
            else if (parent != null)
            {
                catalogConfiguration = parent.catalogConfiguration;
            }

            return new PericopistConfiguration(extractorConfiguration, catalogConfiguration);
        }
        catch (JAXBException e)
        {
            throw new ConfigurationException("The configuration file could not be parsed", e);
        }
    }

    /**
     * This method loads the configuration and evaluates it with the specified context
     *
     * @param resource       the resource of the configuration
     * @param charset        the charset of the configuration
     * @param readTimeout    read timeout of the configuration read operation in milliseconds
     * @param velocityEngine used velocity engine
     * @param context        the velocity context
     *
     * @return pair storing the final url (after redirects) from the configuration and the configuration
     *
     * @throws ConfigurationException if the resource couldn't be read
     */
    private Pair<URL, String> loadConfiguration(URL resource, Charset charset, int readTimeout, VelocityEngine velocityEngine, Context context) throws ConfigurationException
    {
        // reads the configuration file
        String configuration;
        try
        {
            Pair<URL, String> urlConfigPair = Misc.getContent(resource, charset, readTimeout);
            resource = urlConfigPair.getKey();
            configuration = urlConfigPair.getValue();
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

        return new Pair<>(resource, configuration);
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

        NodeList list = document.getElementsByTagName(CONFIGURATION_ROOT_TAG);
        if (list.getLength() == 0)
        {
            throw new ConfigurationException("The configuration file doesn't have a <" + CONFIGURATION_ROOT_TAG + "> node");
        }
        else if (list.getLength() > 1)
        {
            throw new ConfigurationException("The configuration file has more than 1 <" + CONFIGURATION_ROOT_TAG + "> node");
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

    /**
     * This method merges the parent object into the child object.
     *
     * @param child  child which gets values from the parent
     * @param parent the parent object
     *
     * @throws ConfigurationException if the parent class isn't assignable from the child class
     */
    private void mergeObjects(Object child, Object parent) throws ConfigurationException
    {
        if (!parent.getClass().isAssignableFrom(child.getClass()))
        {
            throw new ConfigurationException("The type '" + parent.getClass().getName() + "' isn't assignable from the type '" + parent.getClass().getName() + "'.");
        }

        Class<?> clazz = parent.getClass();
        while (clazz != null)
        {
            for (Field field : clazz.getDeclaredFields())
            {
                if (!field.isAccessible())
                {
                    field.setAccessible(true);
                }

                try
                {
                    Object childFieldObject = field.get(child);
                    Object parentFieldObject = field.get(parent);

                    if (parentFieldObject == null)
                    {
                        // parent value does not exist, skip
                        continue;
                    }

                    if (childFieldObject == null)
                    {
                        // child value does not exist, take parent
                        field.set(child, parentFieldObject);
                        continue;
                    }

                    // parent and child value exists
                    if (field.getType().isArray())
                    {
                        // field is array
                        this.mergeArray(field, child, childFieldObject, parentFieldObject);
                    }
                    else
                    {
                        if (this.isMergeable(field))
                        {
                            // field is a mergeable field
                            this.mergeObjects(childFieldObject, parentFieldObject);
                        }
                    }
                }
                catch (IllegalAccessException e)
                {
                    throw new ConfigurationException("Couldn't merge the current configuration with the parent.", e);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    /**
     * This method returns whether the specified field is mergeable
     *
     * @param field field which shall be checked for mergeable
     *
     * @return whether the field is mergeable
     */
    private boolean isMergeable(Field field)
    {
        Class<?> fieldType = field.getType();
        if (fieldType.isPrimitive())
        {
            return false;
        }

        Mergeable mergeable = field.getAnnotation(Mergeable.class);
        if (mergeable != null)
        {
            return mergeable.value();
        }

        mergeable = fieldType.getAnnotation(Mergeable.class);
        return mergeable != null && mergeable.value();
    }

    /**
     * This method merges two arrays. It merges the parent into the child array
     *
     * @param field    field of the arrays
     * @param instance instance which contains the child array
     * @param child    child array
     * @param parent   parent array
     */
    private void mergeArray(Field field, Object instance, Object child, Object parent) throws IllegalAccessException
    {
        MergeableArray mergeableArray = field.getAnnotation(MergeableArray.class);
        if (mergeableArray == null)
        {
            // don't merge the array
            return;
        }

        int childLength = Array.getLength(child);
        int parentLength = Array.getLength(parent);

        List<Object> list = new ArrayList<>(childLength + parentLength);

        // switch merge mode
        switch (mergeableArray.value())
        {
            case APPEND_BEHIND:
                for (int i = 0; i < parentLength; i++)
                {
                    list.add(Array.get(parent, i));
                }
                for (int i = 0; i < childLength; i++)
                {
                    list.add(Array.get(child, i));
                }
                break;

            case REPLACE_EXISTING_VALUES:
                for (int i = 0; i < parentLength; i++)
                {
                    list.add(Array.get(parent, i));
                }
                for (int i = 0; i < childLength; i++)
                {
                    Object element = Array.get(child, i);

                    int index = list.indexOf(element);
                    if (index < 0)
                    {
                        list.add(element);
                    }
                    else
                    {
                        list.set(index, element);
                    }
                }
                break;

            case APPEND_BEFORE:
                for (int i = 0; i < childLength; i++)
                {
                    list.add(Array.get(child, i));
                }
                for (int i = 0; i < parentLength; i++)
                {
                    list.add(Array.get(parent, i));
                }
                break;

            default:
                throw new IllegalStateException("unknown mergeable array mode " + mergeableArray.value());
        }

        Class<?> componentType = field.getType().getComponentType();
        Object array = list.toArray((Object[])Array.newInstance(Misc.getRelatedClass(componentType), list.size()));

        // convert object array to primitive array
        if (componentType.isPrimitive())
        {
            Object primitiveArray = Array.newInstance(componentType, Array.getLength(array));

            for (int i = 0; i < Array.getLength(primitiveArray); i++)
            {
                Array.set(primitiveArray, i, Array.get(array, i));
            }

            array = primitiveArray;
        }

        field.set(instance, array);
    }

    /**
     * This is a helper class which stores the pericopist configuration and catalog configuration
     */
    private static class PericopistConfiguration
    {
        public final ExtractorConfiguration extractorConfiguration;
        public final CatalogConfiguration catalogConfiguration;

        public PericopistConfiguration(ExtractorConfiguration extractorConfiguration, CatalogConfiguration catalogConfiguration)
        {
            this.extractorConfiguration = extractorConfiguration;
            this.catalogConfiguration = catalogConfiguration;
        }
    }
}
