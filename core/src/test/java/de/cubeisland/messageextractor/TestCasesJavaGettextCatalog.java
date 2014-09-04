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

import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Iterator;

import de.cubeisland.messageextractor.exception.MessageCatalogException;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.format.HeaderConfiguration.MetadataEntry;
import de.cubeisland.messageextractor.format.gettext.GettextCatalogConfiguration;
import de.cubeisland.messageextractor.format.gettext.PlaintextGettextCatalogFormat;
import de.cubeisland.messageextractor.message.MessageStore;
import de.cubeisland.messageextractor.message.TranslatableMessage;
import de.cubeisland.messageextractor.util.ResourceLoader;

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
        Assert.assertNotNull("The configuration isn't a GettextCatalogConfiguration instance.", config);

        Assert.assertEquals(2, config.getPluralAmount());
        Assert.assertEquals("UTF-8", config.getCharset().displayName());
        Assert.assertEquals(false, config.getCreateEmptyTemplate());
        Assert.assertEquals(true, config.getRemoveUnusedMessages());
        Assert.assertEquals(this.catalogFile.getAbsolutePath(), config.getTemplateFile().getAbsolutePath());
        Assert.assertEquals("Hey this are test header comments.\nyou can use the velocity context in your comments.\nfor example: 8\n", config.getHeaderConfiguration().getComments());

        MetadataEntry[] metadataEntries = config.getHeaderConfiguration().getMetadata();
        Assert.assertEquals(3, metadataEntries.length);
        Assert.assertEquals("Project-Id-Version", metadataEntries[0].getKey());
        Assert.assertEquals("PACKAGE VERSION", metadataEntries[0].getValue());
        Assert.assertEquals("POT-Creation-Date", metadataEntries[1].getKey());
        Assert.assertEquals("Last-Translator", metadataEntries[2].getKey());
        Assert.assertEquals("FULL NAME <EMAIL@ADDRESS>", metadataEntries[2].getValue());
    }

    @Test
    public void testCompareJavaConfiguration() throws Exception
    {
        JavaExtractorConfiguration config = (JavaExtractorConfiguration) this.messageCatalog.getExtractorConfiguration();
        Assert.assertNotNull("The configuration isn't a JavaExtractorConfiguration instance.", config);

        Assert.assertEquals("UTF-8", config.getCharset().displayName());
        Assert.assertArrayEquals(System.getProperty("java.class.path").split(File.pathSeparator), config.getClasspathEntries());
        Assert.assertEquals(new File("./src/test/java").getAbsolutePath(), config.getDirectory().getAbsolutePath());
        Assert.assertEquals("de.cubeisland.messageextractor.test.command.User#sendTranslated", config.getTranslatableExpressions()[0].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.command.User#sendTranslatedN", config.getTranslatableExpressions()[1].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.command.Command", config.getTranslatableExpressions()[2].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.TranslatableAnnotation", config.getTranslatableExpressions()[3].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.TranslatableArrayAnnotation", config.getTranslatableExpressions()[4].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.MessageExtractorTest", config.getTranslatableExpressions()[5].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.exception.WrongUsageException", config.getTranslatableExpressions()[6].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.i18n.I18n#translate", config.getTranslatableExpressions()[7].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.i18n.I18n#translateN", config.getTranslatableExpressions()[8].getName());
    }

    @Test
    public void testGenerateCatalog() throws Exception
    {
        // 1. generate new catalog
        this.messageCatalog.generateCatalog();

        // 2. compare new catalog with target one
        PlaintextGettextCatalogFormat gettextCatalogFormat = (PlaintextGettextCatalogFormat) this.messageCatalog.getCatalogFormat();
        Assert.assertNotNull("The catalog format isn't a PlaintextGettextCatalogFormat instance.", gettextCatalogFormat);
        GettextCatalogConfiguration config = (GettextCatalogConfiguration) this.messageCatalog.getCatalogConfiguration();
        Assert.assertNotNull("The configuration isn't a JavaExtractorConfiguration instance.", config);

        // 2.1. load MessageStore instances from both catalogs
        MessageStore currentMessageStore;
        MessageStore targetMessageStore;

        try (FileInputStream fileInputStream = new FileInputStream(config.getTemplateFile()))
        {
            currentMessageStore = gettextCatalogFormat.read(config, fileInputStream);
        }

        config.setTemplateFile(this.targetCatalogFile);
        try (FileInputStream fileInputStream = new FileInputStream(config.getTemplateFile()))
        {
            targetMessageStore = gettextCatalogFormat.read(config, fileInputStream);
        }

        // 2.2. compare MessageStore instances
        Assert.assertEquals(targetMessageStore.size(), currentMessageStore.size());

        Iterator<TranslatableMessage> currentMessages = currentMessageStore.iterator();
        Iterator<TranslatableMessage> targetMessages = targetMessageStore.iterator();

        while (currentMessages.hasNext())
        {
            Assert.assertEquals(targetMessages.next(), currentMessages.next());
        }

        // 3. delete catalog
        Assert.assertTrue(this.catalogFile.delete());
    }
}
