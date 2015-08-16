/**
 * The MIT License
 * Copyright (c) 2013 Cube Island
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

import java.io.File;

import org.cubeengine.pericopist.test.i18n.DefaultI18n;
import org.cubeengine.pericopist.test.i18n.I18n;

public class ConstantTest
{
    private static final int ANSWER_OF_EVERYTHING = 42;
    public static final File WORKING_DIR = new File("./");

    public void method()
    {
        I18n i18n = new DefaultI18n();

        i18n.translate("extracted with a constant from MessageExtractorTest class: " + MessageExtractorTest.TEST_CONST);
        i18n.translate("extracted with a method invocation on a constant from MessageExtractorTest class " + MessageExtractorTest.TEST_CONST.toString());

        i18n.translate("extracted with a private int constant: " + ANSWER_OF_EVERYTHING);

        i18n.translate("extracted with an enum constant: " + TranslatableEnum.FIRST);

        i18n.translate("extracted with a file constant: " + WORKING_DIR);
    }
}
