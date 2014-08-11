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
package de.cubeisland.messageextractor.format.gettext;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.cubeisland.messageextractor.exception.ConfigurationException;
import de.cubeisland.messageextractor.format.AbstractCatalogConfiguration;
import de.cubeisland.messageextractor.format.CatalogFormat;
import de.cubeisland.messageextractor.format.HeaderConfiguration;

/**
 * This configuration is used for creating a gettext catalog which stores translatable messages.
 * <p/>
 * The configuration can be set up with an xml file. {@link de.cubeisland.messageextractor.MessageCatalogFactory#getMessageCatalog(String, java.nio.charset.Charset,
 * org.apache.velocity.context.Context)}
 * TODO add configuration with explanation
 *
 * @see de.cubeisland.messageextractor.MessageCatalogFactory
 * @see de.cubeisland.messageextractor.format.gettext.PlaintextGettextCatalogFormat
 */
@XmlRootElement(name = "catalog")
public class GettextCatalogConfiguration extends AbstractCatalogConfiguration
{
    private HeaderConfiguration headerConfiguration;
    private int pluralAmount = 2;

    /**
     * This method returns the header configuration of the gettext catalog
     *
     * @return header configuration
     */
    public HeaderConfiguration getHeaderConfiguration()
    {
        return headerConfiguration;
    }

    /**
     * This method sets the header configuration of the gettext catalog
     *
     * @param headerConfiguration header configuration
     */
    @XmlElement(name = "header")
    public void setHeaderConfiguration(HeaderConfiguration headerConfiguration)
    {
        this.headerConfiguration = headerConfiguration;
    }

    /**
     * this method returns the amount of plural messages of the source language
     *
     * @return plural message amount
     */
    public int getPluralAmount()
    {
        return pluralAmount;
    }

    /**
     * This method sets the amount of plural messages of the source language
     *
     * @param pluralAmount plural message amount
     */
    @XmlElement(name = "pluralAmount")
    public void setPluralAmount(int pluralAmount)
    {
        this.pluralAmount = pluralAmount;
    }

    @Override
    public Class<? extends CatalogFormat> getCatalogFormatClass()
    {
        return PlaintextGettextCatalogFormat.class;
    }

    @Override
    public void validate() throws ConfigurationException
    {
        if (this.getTemplateFile() == null)
        {
            throw new ConfigurationException("You must specify the path which contains the location of the template");
        }

        if (this.getHeaderConfiguration() != null)
        {
            if (this.getHeaderConfiguration().getCharset() == null)
            {
                this.getHeaderConfiguration().setCharset(this.getCharset());
            }
            this.getHeaderConfiguration().validate();
        }
    }
}
