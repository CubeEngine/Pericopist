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

import de.cubeisland.messageextractor.test.command.Command;
import de.cubeisland.messageextractor.test.command.CommandParameter;

@TranslatableAnnotation("extracted from an annotation with a class target")
public class AnnotationTest
{
    @TranslatableAnnotation("extracted from an annotation with a constant field target")
    private static final String TEST_CONST = "this is a private test constant.";

    @TranslatableAnnotation("extracted from an annotation with a field target")
    private String value;

    @TranslatableAnnotation("extracted from an annotation with a constructor target")
    public AnnotationTest()
    {

    }

    @TranslatableAnnotation("extracted from an annotation with a method target")
    @Command(desc = "extracted from the desc field of the command annotation", usage = "extracted from the usage field of the command annotation")
    @CommandParameter("This message won't be extracted!")
    public void commandTest()
    {

    }

    @AnnotationWithInnerAnnotation(@TranslatableAnnotation("extracted from an annotation as a field of another annotation"))
    @Command(desc = "This annotation tries to write the private constant '" + TEST_CONST + "' into the catalog.")
    public void method(@TranslatableAnnotation("extracted from an annotation with a parameter target") String args)
    {
        @TranslatableAnnotation("extracted from an annotation with a local parameter target")
        int number = 42;
    }
}
