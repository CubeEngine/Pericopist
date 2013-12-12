package de.cubeisland.maven.plugins.messagecatalog.config;

import java.io.File;
import java.util.Collections;
import java.util.Map;

public class SourceConfig
{
    private File directory = new File("./src/main/java");
    private String language = "java";

    private Map<String, String> options = Collections.emptyMap();

    public File getDirectory()
    {
        return directory;
    }

    public void setDirectory(File directory)
    {
        this.directory = directory;
    }

    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    public Map<String, String> getOptions()
    {
        return options;
    }

    public void setOptions(Map<String, String> options)
    {
        this.options = options;
    }
}
