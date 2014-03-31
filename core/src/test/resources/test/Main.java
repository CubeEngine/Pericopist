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
package test;

import test.anot.TestSingleMemberAnnotation;

public class Main
{
    public void main(String[] args)
    {
        I18n i18n = new I18n();
        boolean question = true;

        i18n.sendTranslated("hello everyone");
        i18n.getTranslation("whats up?");
        i18n.getTranslation("whats up?");

        i18n.getTranslation((question ? "true" : "false")); // TODO
        i18n.getTranslation((question ? "right" : "wring") + "answer"); // TODO

        i18n.getTranslationN("hope %s is fine?", "hope you are fine?", getOnlinePersons(), "Phillip");

        SecondTestclass second = new SecondTestclass(i18n, "hello");

        this.getNonTranslation("Bye bye!");
    }

    @TestSingleMemberAnnotation("pre-added")
    private String getNonTranslation(String string, Object... o)
    {
        return String.format(string, o);
    }

    @TestSingleMemberAnnotation("annotationString")
    private int getOnlinePersons()
    {
        return 2;
    }
}
