package de.cubeisland.maven.plugins.messagecatalog.format;

import org.w3c.dom.Node;

public interface CatalogConfig
{
    void parse(Node node);

    String getFormat();
}
