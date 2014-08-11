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
package de.cubeisland.messageextractor.format;

import java.io.File;

import de.cubeisland.messageextractor.configuration.Configuration;

/**
 * An CatalogConfiguration is used to set a CatalogFormat instance up.
 * One CatalogConfiguration class relates to one CatalogFormat class.
 *
 * @see de.cubeisland.messageextractor.format.CatalogFormat
 */
public interface CatalogConfiguration extends Configuration
{
    /**
     * This method returns template file of the catalog
     *
     * @return template file
     */
    File getTemplateFile();

    /**
     * This method sets the template file of the catalog
     * @param file template file
     */
    void setTemplateFile(File file);

    /**
     * This method returns the related CatalogFormat class.
     *
     * @return related CatalogFormat instance
     */
    Class<? extends CatalogFormat> getCatalogFormatClass();
}
