/**
 * This file is part of messageextractor-maven-plugin.
 * messageextractor-maven-plugin is licensed under the GNU General Public License Version 3.
 *
 * messageextractor-maven-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * messageextractor-maven-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with messageextractor-maven-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.cubeisland.maven.plugins.messageextractor.mojo;

import de.cubeisland.messageextractor.MessageCatalog;
import de.cubeisland.messageextractor.exception.MessageCatalogException;

/**
 * This Mojo implements the goal generate. This goal creates a message catalog
 *
 * @goal generate
 */
public class GenerateMojo extends AbstractMessageExtractorMojo
{
    /**
     * This method generates the message catalog
     *
     * @param catalog the MessageCatalog instance
     *
     * @throws MessageCatalogException
     */
    @Override
    public void doExecute(MessageCatalog catalog) throws MessageCatalogException
    {
        catalog.generateCatalog();
    }
}
