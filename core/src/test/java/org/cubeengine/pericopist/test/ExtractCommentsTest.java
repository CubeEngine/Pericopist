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

import org.cubeengine.pericopist.test.command.Command;
import org.cubeengine.pericopist.test.i18n.DefaultI18n;
import org.cubeengine.pericopist.test.i18n.I18n;

public class ExtractCommentsTest
{
    /// This multi line comment will be
    /// extracted and added at the desc and
    /// the usage method.
    @Command(desc = "The desc of a command with extracted comments", usage = "the usage of a command with extracted comments")
    public void method()
    {
        @TranslatableAnnotation("An annotation with an extracted comment") /// extract me
        I18n i18n = new DefaultI18n();

        /// multi line
        /// comment that
        /// will be
        /// extracted
        i18n.translate("A message with an extracted comment above.");

        i18n.translate("A message with an extracted comment at the right."); /// extract me

        /// I won't be extracted
        /// because the other comment has a higher priority
        i18n.translate("A message with an extracted comment above and at the right"); /// I'll be extracted

        i18n.translate("A message with an extracted comment at the right and another translatable message beneath."); /// extract me for the message at the left, but not for the message beneath.
        i18n.translate("A message with an extracted comment above which is in a line with preceding java code.");
    }
}
