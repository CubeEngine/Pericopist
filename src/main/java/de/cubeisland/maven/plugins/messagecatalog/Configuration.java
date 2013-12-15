package de.cubeisland.maven.plugins.messagecatalog;

import org.w3c.dom.Node;

import de.cubeisland.maven.plugins.messagecatalog.exception.MissingNodeException;

public interface Configuration
{
    void parse(Node node) throws MissingNodeException;
}
