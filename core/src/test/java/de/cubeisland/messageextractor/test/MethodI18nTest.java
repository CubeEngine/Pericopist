/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Phillip Schichtel, Stefan Wolf
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
package de.cubeisland.messageextractor.test;

import de.cubeisland.messageextractor.test.i18n.DefaultI18n;
import de.cubeisland.messageextractor.test.i18n.ExtendedI18n;
import de.cubeisland.messageextractor.test.i18n.I18n;

public class MethodI18nTest
{
    public void testExtendedI18nPolymorphism()
    {
        I18n i18n = new ExtendedI18n();

        i18n.translate("extracted from I18n interface with polymorphism. uses ExtendedI18n class");
        i18n.translateN(0, "singular of a plural message from polymorphism test", "plural of the plural message from polymorphism test");
    }

    public void testExtendedI18n()
    {
        ExtendedI18n i18n = new ExtendedI18n();

        i18n.translate("extracted from ExtendedI18n class");
        i18n.translateN(0, "singular of a plural message from ExtendedI18n class", "plural of the plural message from ExtendedI18n class");
    }

    public void testDefaultI18n()
    {
        DefaultI18n i18n = new DefaultI18n();

        i18n.translate("extracted from DefaultI18n class");
        i18n.translateN(0, "singular of a plural message from DefaultI18n class", "plural of the plural message from DefaultI18n class");
    }
}
