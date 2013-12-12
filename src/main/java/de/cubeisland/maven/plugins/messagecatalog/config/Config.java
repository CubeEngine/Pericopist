package de.cubeisland.maven.plugins.messagecatalog.config;

public class Config
{
    private CatalogConfig catalog;
    private SourceConfig source;

    public Config(SourceConfig source, CatalogConfig catalog)
    {
        this.source = source;
        this.catalog = catalog;
    }

    public CatalogConfig getCatalog()
    {
        return catalog;
    }

    public SourceConfig getSource()
    {
        return source;
    }
}
