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
package org.cubeengine.pericopist;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import javafx.util.Pair;
import org.cubeengine.pericopist.util.Misc;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MiscTest
{
    @Test
    public void getRelativizedFileTest()
    {
        File base = new File("src/main/java");
        File file = new File("src/main/java/org/cubeegine/pericopist/Pericopist.java");

        assertEquals(file.getPath().substring(base.getPath().length() + 1), Misc.getRelativizedFile(base, file).getPath());
    }

    @Test
    public void resolveUrlTest()
    {
        assertEquals("http://www.google.de/test/parent.xml", Misc.resolvePath("http://www.google.de/test/config.xml", "parent.xml"));
        assertEquals("http://www.google.de/test/parent.xml", Misc.resolvePath("http://www.google.de/test/config.xml", "./parent.xml"));
        assertEquals("http://www.google.de/test/blub/parent.xml", Misc.resolvePath("http://www.google.de/test/config.xml", "blub/parent.xml"));
        assertEquals("http://www.google.de/test/parent.xml", Misc.resolvePath("http://www.google.de/test/blub/config.xml", "../parent.xml"));
        assertEquals("http://www.google.de/parent.xml", Misc.resolvePath("http://www.google.de/test/blub/config.xml", "../../parent.xml"));
        assertEquals("http://www.google.de/blub/parent.xml", Misc.resolvePath("http://www.google.de/test/what/do/I/know/config.xml", "/blub/parent.xml"));
        assertEquals("http://www.cubyte.org/test/parent.xml", Misc.resolvePath("http://www.google.de/test/blub/config.xml", "http://www.cubyte.org/test/parent.xml"));
        assertEquals("http://www.cubyte.org/test/parent.xml", Misc.resolvePath("file://D:/files/config.xml", "http://www.cubyte.org/test/parent.xml"));
        assertEquals("http://www.cubyte.org/test/parent.xml", Misc.resolvePath("D:/files/config.xml", "http://www.cubyte.org/test/parent.xml"));
        assertEquals("D:/files/blub/parent.xml", Misc.resolvePath("D:/files/config.xml", "blub/parent.xml"));
        assertEquals("D:/files/parent.xml", Misc.resolvePath("D:/files/config.xml", "parent.xml"));
        assertEquals("D:/parent.xml", Misc.resolvePath("D:/files/config.xml", "../parent.xml"));
    }

    @Test
    public void getResourceTest() throws IOException
    {
        String path = "src/main";

        File file = new File(path);
        URL url = Misc.getResource(path);
        assertNotNull(url);

        assertEquals(file.toURI().toURL().toExternalForm(), url.toExternalForm());

        // ---------

        path = "http://www.google.de";
        url = Misc.getResource(path);
        assertNotNull(url);
        assertEquals(path, url.toExternalForm());

        // --------

        path = "https://www.google.de";
        url = Misc.getResource(path);
        assertNotNull(url);
        assertEquals(path, url.toExternalForm());
    }

    @Test
    public void getContentWithRedirectionTest() throws IOException
    {
        final URL startUrl = new URL("http://raw.githubusercontent.com/CubeEngine/Pericopist/master/core/src/test/resources/configuration.xml");
        final Charset charset = Charset.forName("UTF-8");
        final int readTimeout = 5000;

        Pair<URL, String> content = Misc.getContent(startUrl, charset, readTimeout);
        assertNotEquals(startUrl.toExternalForm(), content.getKey());

        Pair<URL, String> secondContent = Misc.getContent(content.getKey(), charset, readTimeout);
        assertEquals(content.getKey(), secondContent.getKey());

        assertEquals(content.getValue(), secondContent.getValue());
    }

    @Test
    public void getRelatedClassTest()
    {
        assertEquals(File.class, Misc.getRelatedClass(File.class));
        assertEquals(String.class, Misc.getRelatedClass(String.class));
        assertEquals(Boolean.class, Misc.getRelatedClass(boolean.class));
        assertEquals(Byte.class, Misc.getRelatedClass(byte.class));
        assertEquals(Character.class, Misc.getRelatedClass(char.class));
        assertEquals(Double.class, Misc.getRelatedClass(double.class));
        assertEquals(Float.class, Misc.getRelatedClass(float.class));
        assertEquals(Integer.class, Misc.getRelatedClass(int.class));
        assertEquals(Long.class, Misc.getRelatedClass(long.class));
        assertEquals(Short.class, Misc.getRelatedClass(short.class));
        assertEquals(Void.class, Misc.getRelatedClass(void.class));
    }
}
