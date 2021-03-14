/*
 * The MIT License
 * Copyright © 2013 Cube Island
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.cubeengine.pericopist.test;

import org.cubeengine.pericopist.test.i18n.DefaultI18n;
import org.cubeengine.pericopist.test.i18n.I18n;

public class MessageExtractorTest
{
    public static final String TEST_CONST = "I'm a test const for another test class.";

    public static void main(String[] args)
    {
        new MessageExtractorTest("This is the start of the messageextractor test");
    }

    public MessageExtractorTest(String startMessage)
    {
        I18n i18n = new DefaultI18n();

        i18n.translate("welcome %s to the messageextractor test.", "YOUR_NAME");

        i18n.translate("The main task of the project is to generate a message catalog containing all the string literals of a project by parsing its source tree.");
        i18n.translate("This is the core module of the messageextractor.");
        i18n.translate("One can extract messages from special methods, constructors and annotation fields");
        i18n.translate("The test is an example of how it works and shows examples.");

        i18n.translateN(0, "One monkey is better than sheep", "We've %d monkeys", 0);
    }
}
