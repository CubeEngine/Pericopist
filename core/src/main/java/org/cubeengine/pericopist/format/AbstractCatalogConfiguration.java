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
package org.cubeengine.pericopist.format;

import java.io.File;
import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.cubeengine.pericopist.util.XmlCharsetAdapter;

/**
 * This is an abstract {@link CatalogConfiguration} class which stores information which is needed by every configuration.
 */
public abstract class AbstractCatalogConfiguration implements CatalogConfiguration
{
    private File templateFile;
    private Boolean removeUnusedMessages;
    private Boolean createEmptyTemplate;
    private Charset charset;

    @Override
    public final File getTemplateFile()
    {
        return this.templateFile;
    }

    /**
     * This method sets the template file of the catalog
     *
     * @param file template file
     */
    @XmlElement(name = "template")
    public final void setTemplateFile(File file)
    {
        this.templateFile = file;
    }

    /**
     * This method sets whether the unused messages shall be removed from the catalog
     *
     * @param removeUnusedMessages whether unused messages shell be removed
     */
    @XmlElement(name = "removeUnusedMessages")
    public final void setRemoveUnusedMessages(boolean removeUnusedMessages)
    {
        this.removeUnusedMessages = removeUnusedMessages;
    }

    /**
     * This method returns whether the unused messages shall be removed from the catalog
     *
     * @return whether the unused messages shall be removed
     */
    public final boolean getRemoveUnusedMessages()
    {
        if (this.removeUnusedMessages == null)
        {
            return true;
        }
        return this.removeUnusedMessages;
    }

    /**
     * This method sets whether an empty catalog file shall be created.
     *
     * @param value whether an empty catalog file shall be created
     */
    @XmlElement(name = "createEmptyTemplate")
    public final void setCreateEmptyTemplate(boolean value)
    {
        this.createEmptyTemplate = value;
    }

    /**
     * This method returns whether an empty catalog file shall be created.
     *
     * @return whether an empty catalog file shall be created
     */
    public final boolean getCreateEmptyTemplate()
    {
        if (this.createEmptyTemplate == null)
        {
            return false;
        }
        return this.createEmptyTemplate;
    }

    @XmlAttribute(name = "charset")
    @XmlJavaTypeAdapter(XmlCharsetAdapter.class)
    @Override
    public final void setCharset(Charset charset)
    {
        this.charset = charset;
    }

    @Override
    public final Charset getCharset()
    {
        return this.charset;
    }
}
