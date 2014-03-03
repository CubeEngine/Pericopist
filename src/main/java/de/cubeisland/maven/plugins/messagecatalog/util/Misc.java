package de.cubeisland.maven.plugins.messagecatalog.util;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
    {}

    public static List<File> scanFilesRecursive(File baseDir)
    {
        return scanFilesRecursive(baseDir, DUMMY_FILTER);
    }

    public static List<File> scanFilesRecursive(File baseDir, FileFilter filter)
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

    private static void scanFilesRecursive0(File directory, List<File> files, FileFilter filter)
    {
        for (File file : directory.listFiles())
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

    public static VelocityEngine getVelocityEngine(File file)
    {
        Properties properties = new Properties();
        properties.put("resource.loader", "file");
        properties.put("file.resource.loader.class", FileResourceLoader.class.getName());
        properties.put("file.resource.loader.description", "Velocity File Resource Loader");
        if (file.getParentFile() != null)
        {
            properties.put("file.resource.loader.path", file.getParentFile().getAbsolutePath());
        }
        properties.put("file.resource.loader.cache", false);

        return new VelocityEngine(properties);
    }
}
