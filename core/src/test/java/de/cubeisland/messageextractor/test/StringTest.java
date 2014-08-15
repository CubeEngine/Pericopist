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

import java.io.File;
import java.util.Locale;

import de.cubeisland.messageextractor.message.Occurrence;
import de.cubeisland.messageextractor.test.i18n.DefaultI18n;
import de.cubeisland.messageextractor.test.i18n.I18n;

public class StringTest
{
    public void method()
    {
        I18n i18n = new DefaultI18n();

        i18n.translate("a normal string");
        i18n.translate("a concatenated " + "string");
        i18n.translate(true ? "yes" : "no");

        i18n.translate("an integer: " + 1);
        i18n.translate("a float: " + 1.04f);
        i18n.translate("a double: " + 1.04d);

        i18n.translate("method invocation: " + "invocation".toUpperCase(Locale.ENGLISH).replaceFirst("I", "method i"));
        i18n.translate("a static method invocation: " + String.valueOf("hello"));
        i18n.translate("new class invocation: " + new Occurrence(new File("testFile.tmp"), 42));
        i18n.translate("new class + method invocation: " + new StringBuilder().append("this").append(' ').append("is ").append('a').append(" test").toString());

        i18n.translate("method invocation with more arguments than parameter: " + String.format("%s %s %s", "it's", "a", "test"));
        i18n.translate("method invocation with array parameters: " + String.valueOf(new char[] {'t', 'e', 's', 't'}));
        i18n.translate("method invocation without last parameter: " + String.format("it's a test"));
        i18n.translate("method invocation without last parameter: " + String.format("it's a %s", "test"));
        i18n.translate("a null test " + testNullMethod(null));

        i18n.translate("calculations are not supported: " + (4 - 3));
    }

    private static String testNullMethod(String test)
    {
        return "test";
    }
}
