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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class Misc
{
    private static final FileFilter DUMMY_FILTER = new FileFilter()
    {
        public boolean accept(File file)
        {
            return true;
        }
    };

    private Misc()
    {
    }

    public static List<File> scanFilesRecursive(File baseDir) throws IOException
    {
        return scanFilesRecursive(baseDir, DUMMY_FILTER);
    }

    public static List<File> scanFilesRecursive(File baseDir, FileFilter filter) throws IOException
    {
        if (baseDir == null)
        {
            throw new NullPointerException("The base directory must not be null!");
        }
        if (filter == null)
        {
            throw new NullPointerException("The filter must not be null!");
        }
        if (!baseDir.isDirectory())
        {
            throw new IllegalArgumentException("The base directory must actually be a directory...");
        }

        List<File> files = new LinkedList<File>();
        scanFilesRecursive0(baseDir, files, filter);

        return files;
    }

    private static void scanFilesRecursive0(File directory, List<File> files, FileFilter filter) throws IOException
    {
        final File[] directoryListing = directory.listFiles();
        if (directoryListing == null)
        {
            throw new IOException("Failed to enlist the directory '" + directory.getPath() + "' !");
        }
        for (File file : directoryListing)
        {
            if (file.isDirectory())
            {
                scanFilesRecursive0(file, files, filter);
            }
            else if (filter.accept(file))
            {
                files.add(file);
            }
        }
    }

    public static char[] parseFileToCharArray(File file) throws IOException
    {
        BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
        StringBuilder sb = new StringBuilder();
        byte[] buffer = new byte[4096];

        int bytesRead = 0;
        while ((bytesRead = stream.read(buffer)) > -1)
        {
            if (bytesRead > 0)
            {
                sb.append(new String(buffer, 0, bytesRead));
            }
        }

        stream.close();
        return sb.toString().toCharArray();
    }

    public static File getRelativizedFile(File base, File file)
    {
        return new File(base.toURI().relativize(file.toURI()).getPath());
    }

    public static URL getResource(String resource)
    {
        File file = new File(resource);
        if (file.exists() && file.canRead())
        {
            try
            {
                return file.toURI().toURL();
            }
            catch (MalformedURLException ignored)
            {
            }
        }

        try
        {
            URL url = new URL(resource);
            url.openStream().close();
            return url;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public static String getContent(URL url) throws IOException
    {
        URLConnection connection = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder content = new StringBuilder();
        String inputLine;

        while ((inputLine = reader.readLine()) != null)
        {
            content.append(inputLine);
            content.append('\n');
        }

        reader.close();
        return content.toString();
    }
}
