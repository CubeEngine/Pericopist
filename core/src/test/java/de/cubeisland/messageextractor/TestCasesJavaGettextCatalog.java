/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Iterator;

import de.cubeisland.messageextractor.exception.MessageCatalogException;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.format.CatalogConfiguration;
import de.cubeisland.messageextractor.format.CatalogFormat;
import de.cubeisland.messageextractor.format.HeaderConfiguration.MetadataEntry;
import de.cubeisland.messageextractor.format.gettext.GettextCatalogConfiguration;
import de.cubeisland.messageextractor.format.gettext.PlaintextGettextCatalogFormat;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.TranslatableMessage;
import de.cubeisland.messageextractor.util.ResourceLoader;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TestCasesJavaGettextCatalog
{
    private File targetCatalogFile;
    private File catalogFile;
    private MessageCatalog messageCatalog;

    @Before
    public void setUp() throws MessageCatalogException
    {
        ToolContext toolContext = new ToolManager(true).createContext();
        toolContext.put("resource", new ResourceLoader());

        MessageCatalogFactory factory = new MessageCatalogFactory();
        this.messageCatalog = factory.getMessageCatalog("./src/test/resources/configuration.xml", Charset.forName("UTF-8"), toolContext);

        this.targetCatalogFile = new File("./src/test/resources/target_catalog.pot");
        this.catalogFile = new File("./src/test/resources/messages.pot");
    }

    @Test
    public void testCompareGettextConfiguration() throws Exception
    {
        GettextCatalogConfiguration config = (GettextCatalogConfiguration) this.messageCatalog.getCatalogConfiguration();
        assertNotNull("The configuration isn't a GettextCatalogConfiguration instance.", config);

        assertEquals(2, config.getPluralAmount());
        assertEquals("UTF-8", config.getCharset().displayName());
        assertEquals(false, config.getCreateEmptyTemplate());
        assertEquals(true, config.getRemoveUnusedMessages());
        assertEquals(this.catalogFile.getAbsolutePath(), config.getTemplateFile().getAbsolutePath());
        assertEquals("Hey this are test header comments.\nyou can use the velocity context in your comments.\nfor example: 8\n", config.getHeaderConfiguration().getComments());

        MetadataEntry[] metadataEntries = config.getHeaderConfiguration().getMetadata();
        assertEquals(3, metadataEntries.length);
        assertEquals("Project-Id-Version", metadataEntries[0].getKey());
        assertEquals("PACKAGE VERSION", metadataEntries[0].getValue());
        assertEquals("POT-Creation-Date", metadataEntries[1].getKey());
        assertEquals("Last-Translator", metadataEntries[2].getKey());
        assertEquals("FULL NAME <EMAIL@ADDRESS>", metadataEntries[2].getValue());
    }

    @Test
    public void testCompareJavaConfiguration() throws Exception
    {
        JavaExtractorConfiguration config = (JavaExtractorConfiguration) this.messageCatalog.getExtractorConfiguration();
        assertNotNull("The configuration isn't a JavaExtractorConfiguration instance.", config);

        assertEquals("UTF-8", config.getCharset().displayName());
        assertArrayEquals(System.getProperty("java.class.path").split(File.pathSeparator), config.getClasspathEntries());
        assertEquals(new File("./src/test/java").getAbsolutePath(), config.getDirectory().getAbsolutePath());
        assertEquals("de.cubeisland.messageextractor.test.command.User#sendTranslated", config.getJavaExpressions()[0].getName());
        assertEquals("de.cubeisland.messageextractor.test.command.User#sendTranslatedN", config.getJavaExpressions()[1].getName());
        assertEquals("de.cubeisland.messageextractor.test.command.Command", config.getJavaExpressions()[2].getName());
        assertEquals("de.cubeisland.messageextractor.test.TranslatableAnnotation", config.getJavaExpressions()[3].getName());
        assertEquals("de.cubeisland.messageextractor.test.TranslatableArrayAnnotation", config.getJavaExpressions()[4].getName());
        assertEquals("de.cubeisland.messageextractor.test.MessageExtractorTest", config.getJavaExpressions()[5].getName());
        assertEquals("de.cubeisland.messageextractor.test.exception.WrongUsageException", config.getJavaExpressions()[6].getName());
        assertEquals("de.cubeisland.messageextractor.test.i18n.I18n#translate", config.getJavaExpressions()[7].getName());
        assertEquals("de.cubeisland.messageextractor.test.i18n.I18n#translateC", config.getJavaExpressions()[8].getName());
        assertEquals("de.cubeisland.messageextractor.test.i18n.I18n#translateN", config.getJavaExpressions()[9].getName());
        assertEquals("de.cubeisland.messageextractor.test.TranslatableContextAnnotation", config.getJavaExpressions()[10].getName());
    }

    @Test
    public void testGenerateCatalog() throws Exception
    {
        // 1. generate new catalog
        this.messageCatalog.generateCatalog();

        // 2. compare new catalog with target one
        CatalogFormat catalogFormat = this.messageCatalog.getCatalogFormat();
        assertNotNull(catalogFormat);
        assertEquals(PlaintextGettextCatalogFormat.class, catalogFormat.getClass());

        CatalogConfiguration configuration = this.messageCatalog.getCatalogConfiguration();
        assertNotNull(configuration);
        assertEquals(GettextCatalogConfiguration.class, configuration.getClass());

        GettextCatalogConfiguration config = (GettextCatalogConfiguration) configuration;

        // 2.1. load MessageStore instances from both catalogs
        Method readMethod = this.messageCatalog.getClass().getDeclaredMethod("readCatalog");
        readMethod.setAccessible(true);

        Object object = readMethod.invoke(this.messageCatalog);
        assertNotNull(object);
        assertEquals(MessageStore.class, object.getClass());

        MessageStore currentMessageStore = (MessageStore) object;

        config.setTemplateFile(this.targetCatalogFile);
        object = readMethod.invoke(this.messageCatalog);
        assertNotNull(object);
        assertEquals(MessageStore.class, object.getClass());

        MessageStore targetMessageStore = (MessageStore) object;

        // 2.2. compare MessageStore instances
        assertEquals(targetMessageStore.size(), currentMessageStore.size());

        Iterator<TranslatableMessage> currentMessages = currentMessageStore.iterator();
        Iterator<TranslatableMessage> targetMessages = targetMessageStore.iterator();

        while (currentMessages.hasNext())
        {
            assertEquals(targetMessages.next(), currentMessages.next());
        }

        // 3. delete catalog
        assertTrue(this.catalogFile.delete());
    }

    @Test
    public void testUpdateCatalog() throws Exception
    {
        CatalogConfiguration configuration = this.messageCatalog.getCatalogConfiguration();
        assertNotNull(configuration);
        assertEquals(GettextCatalogConfiguration.class, configuration.getClass());

        GettextCatalogConfiguration config = (GettextCatalogConfiguration) configuration;
        config.setTemplateFile(this.targetCatalogFile);

        Method readMethod = this.messageCatalog.getClass().getDeclaredMethod("readCatalog");
        readMethod.setAccessible(true);

        Object o = readMethod.invoke(this.messageCatalog);
        assertNotNull(o);
        assertEquals(MessageStore.class, o.getClass());

        MessageStore messageStore = (MessageStore) o;
        int length = messageStore.size();

        Method parseSourceCode = this.messageCatalog.getClass().getDeclaredMethod("parseSourceCode", MessageStore.class);
        parseSourceCode.setAccessible(true);

        o = parseSourceCode.invoke(this.messageCatalog, messageStore);
        assertNotNull(o);
        assertEquals(MessageStore.class, o.getClass());
        messageStore = (MessageStore) o;

        assertEquals(length, messageStore.size());

        final Class<?> gettextMessageClass = this.getClass().getClassLoader().loadClass("de.cubeisland.messageextractor.format.gettext.GettextMessage");
        final Class<?> translatableGettextMessageClass = this.getClass().getClassLoader().loadClass("de.cubeisland.messageextractor.format.gettext.TranslatableGettextMessage");
        assertNotNull(gettextMessageClass);
        assertNotNull(translatableGettextMessageClass);

        Method hasChanges = translatableGettextMessageClass.getMethod("hasChanges");
        hasChanges.setAccessible(true);

        int notTranslatableGettextMessageCount = 0;

        for (TranslatableMessage message : messageStore)
        {
            assertTrue(gettextMessageClass.isAssignableFrom(message.getClass()));

            if (translatableGettextMessageClass.isInstance(message))
            {
                assertFalse((boolean) hasChanges.invoke(message));
            }
            else
            {
                notTranslatableGettextMessageCount++;
            }
        }

        assertEquals(1, notTranslatableGettextMessageCount);
    }
}
