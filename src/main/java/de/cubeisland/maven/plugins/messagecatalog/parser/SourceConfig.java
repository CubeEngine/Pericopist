package de.cubeisland.maven.plugins.messagecatalog.parser;

import org.w3c.dom.Node;

public interface SourceConfig
{
    void parse(Node node);

    String getLanguage();
}
