package de.cubeisland.maven.plugins.messagecatalog.util;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

public class Misc
{
    private static final FileFilter DUMMY_FILTER = new FileFilter() {

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
}
