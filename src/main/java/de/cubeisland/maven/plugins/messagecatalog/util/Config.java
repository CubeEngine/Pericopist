package de.cubeisland.maven.plugins.messagecatalog.util;

import java.io.File;
import java.util.Map;

public class Config
{
    // java specific
    public static final String TRANSLATABLE_METHODS = "methods";
    public static final String TRANSLATABLE_ANNOTATIONS = "annotations";

    public File sourcePath;
    public String templateFile;
    public String language;
    public String outputFormat;
    public boolean removeUnusedMessages;

    public Map<String, String> options;
}
