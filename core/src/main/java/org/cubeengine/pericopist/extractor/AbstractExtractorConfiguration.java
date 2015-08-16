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
package org.cubeengine.pericopist.extractor;

import java.io.File;
import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.cubeengine.pericopist.util.XmlCharsetAdapter;

/**
 * This is an abstract ExtractorConfiguration class which stores information which is needed by every configuration.
 */
public abstract class AbstractExtractorConfiguration implements ExtractorConfiguration
{
    private File directory;
    private Charset charset;

    /**
     * This method sets the directory of the source files.
     * This directory will be scanned for the source files.
     *
     * @param directory directory of the source files
     */
    @XmlElement(name = "directory")
    public final void setDirectory(File directory)
    {
        this.directory = directory;
    }

    /**
     * This method returns the directory which stores the source files.
     *
     * @return directory of the source files
     */
    public final File getDirectory()
    {
        if (this.directory == null)
        {
            // default value
            return new File("./src/main/java");
        }
        return this.directory;
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
