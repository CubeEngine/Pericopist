package de.cubeisland.maven.plugins.messagecatalog.util;

import java.io.File;
import java.util.Map;

public class Config
{
    private final String sourceLanguage;
    private final File sourcePath;
    private final String templateFile;
    private final String outputFormat;
    private final boolean removeUnusedMessages;

    private Map<String, String> options;

    public Config(String language, File sourcePath, String templateFile, String outputFormat, boolean removeUnusedMessages, Map<String, String> options)
    {
        this.sourceLanguage = language;
        this.sourcePath = sourcePath;
        this.templateFile = templateFile;
        this.outputFormat = outputFormat;
        this.removeUnusedMessages = removeUnusedMessages;
        this.options = options;
    }

    public String getSourceLanguage()
    {
        return this.sourceLanguage;
    }

    public File getSourcePath()
    {
        return this.sourcePath;
    }

    public String getTemplateFile()
    {
        return this.templateFile;
    }

    public String getOutputFormat()
    {
        return this.outputFormat;
    }

    public boolean removeUnusedMessages()
    {
        return this.removeUnusedMessages;
    }

    public Map<String, String> getOptions()
    {
        return this.options;
    }
}
