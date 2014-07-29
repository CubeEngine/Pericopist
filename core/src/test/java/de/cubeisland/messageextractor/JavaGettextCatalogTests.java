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

import java.io.File;
import java.nio.charset.Charset;

import de.cubeisland.messageextractor.exception.MessageCatalogException;
import de.cubeisland.messageextractor.extractor.java.configuration.JavaExtractorConfiguration;
import de.cubeisland.messageextractor.format.gettext.GettextCatalogConfiguration;

public class JavaGettextCatalogTests
{
    private MessageCatalog messageCatalog;

    @Before
    public void setUp() throws MessageCatalogException
    {
        ToolContext toolContext = new ToolManager(true).createContext();

        MessageCatalogFactory factory = new MessageCatalogFactory();
        this.messageCatalog = factory.getMessageCatalog("./src/test/resources/example.xml", Charset.forName("UTF-8"), toolContext);
    }

    @Test
    public void testCompareGettextConfiguration() throws Exception
    {
        GettextCatalogConfiguration config = (GettextCatalogConfiguration) this.messageCatalog.getCatalogConfiguration();
        Assert.assertNotNull("The configuration isn't a GettextCatalogConfiguration instance.", config);

        Assert.assertEquals(4, config.getPluralAmount());
        Assert.assertEquals("UTF-8", config.getCharset().displayName());
        Assert.assertEquals(false, config.getCreateEmptyTemplate());
        Assert.assertEquals(true, config.getRemoveUnusedMessages());
        Assert.assertEquals(new File("./src/test/resources/messages.pot").getAbsolutePath(), config.getTemplateFile().getAbsolutePath());
        Assert.assertEquals("UTF-8", config.getHeaderConfiguration().getCharset().displayName());
        Assert.assertEquals("./src/test/resources/header.txt", config.getHeaderConfiguration().getComments());
        Assert.assertEquals("Project-Id-Version", config.getHeaderConfiguration().getMetadata()[0].getKey());
        Assert.assertEquals("POT-Creation-Date", config.getHeaderConfiguration().getMetadata()[1].getKey());
        Assert.assertEquals("Last-Translator", config.getHeaderConfiguration().getMetadata()[2].getKey());

    }

    @Test
    public void testCompareJavaConfiguration() throws Exception
    {
        JavaExtractorConfiguration config = (JavaExtractorConfiguration) this.messageCatalog.getExtractorConfiguration();
        Assert.assertNotNull("The configuration isn't a JavaExtractorConfiguration instance.", config);

        Assert.assertEquals("UTF-8", config.getCharset().displayName());
        Assert.assertEquals(System.getProperty("java.class.path"), config.getClasspath());
        Assert.assertEquals(new File("./src/test/java").getAbsolutePath(), config.getDirectory().getAbsolutePath());
        Assert.assertEquals("de.cubeisland.messageextractor.test.i18n.I18n#translate", config.getTranslatableExpressions()[0].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.i18n.I18n#translateN", config.getTranslatableExpressions()[1].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.command.User#sendTranslated", config.getTranslatableExpressions()[2].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.command.User#sendTranslatedN", config.getTranslatableExpressions()[3].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.command.Command", config.getTranslatableExpressions()[4].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.MessageExtractorTest", config.getTranslatableExpressions()[5].getName());
        Assert.assertEquals("de.cubeisland.messageextractor.test.exception.WrongUsageException", config.getTranslatableExpressions()[6].getName());
    }
}
