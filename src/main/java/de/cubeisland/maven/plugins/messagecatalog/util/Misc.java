package de.cubeisland.maven.plugins.messagecatalog.util;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Misc
{
    private static final FileFilter DUMMY_FILTER = new FileFilter() {
        @Override
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
        while((bytesRead = stream.read(buffer)) > -1)
        {
            if(bytesRead > 0)
            {
                sb.append(new String(buffer, 0, bytesRead));
            }
        }

        stream.close();
        return sb.toString().toCharArray();
    }
}
