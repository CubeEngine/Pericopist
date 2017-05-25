/*
 * pericopist-maven-plugin - A maven plugin to extract messages from your source code and generate message catalogs.
 * Copyright © 2013 Cube Island (development@cubeisland.de)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.cubeengine.maven.plugins.pericopist.mojo;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.cubeengine.pericopist.Pericopist;
import org.cubeengine.pericopist.exception.PericopistException;

/**
 * This Mojo implements the goal generate. This goal creates a message catalog
 */
@Mojo(
    name = "generate",
    threadSafe = true,
    requiresDependencyResolution = ResolutionScope.TEST
)
@SuppressWarnings("JavaDoc")
public class GenerateMojo extends AbstractPericopistMojo
{
    /**
     * This method generates the message catalog
     *
     * @param catalog the {@link Pericopist} instance
     *
     * @throws PericopistException
     */
    @Override
    public void doExecute(Pericopist catalog) throws PericopistException
    {
        catalog.generateCatalog();
    }
}
