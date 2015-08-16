/**
 * This file is part of pericopist-maven-plugin.
 *
 * pericopist-maven-plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * pericopist-maven-plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with pericopist-maven-plugin.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * This file is part of messageextractor-maven-plugin.
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
package org.cubeengine.maven.plugins.pericopist.mojo;

import org.cubeengine.pericopist.Pericopist;
import org.cubeengine.pericopist.exception.PericopistException;

/**
 * This Mojo implements the goal generate. This goal creates a message catalog
 *
 * @goal generate
 */
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
