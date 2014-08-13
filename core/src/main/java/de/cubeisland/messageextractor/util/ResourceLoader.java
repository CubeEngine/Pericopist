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
package de.cubeisland.messageextractor.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * The resource loader which helps to load resources within a velocity template.
 */
public class ResourceLoader
{
    /**
     * This method loads a resource.
     *
     * @param path the path of the resource
     *
     * @return the content of the resource
     *
     * @throws IOException if the resource couldn't be loaded
     */
    public static String load(String path) throws IOException
    {
        return load(path, "UTF-8");
    }

    /**
     * This method loads a resource
     *
     * @param path        the path of the resource
     * @param charsetName the charset of the resource
     *
     * @return the content of the resource
     *
     * @throws IOException if the resource couldn't be loaded
     */
    public static String load(String path, String charsetName) throws IOException
    {
        URL url = Misc.getResource(path);
        if (url == null)
        {
            throw new FileNotFoundException("The resource '" + path + "' was not found in file system or as URL.");
        }

        return Misc.getContent(url, Charset.forName(charsetName));
    }
}
