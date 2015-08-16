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

import org.cubeengine.pericopist.test.exception.MissingParameterException;
import org.cubeengine.pericopist.test.exception.WrongUsageException;

public class ConstructorTest
{
    public void method()
    {
        new MessageExtractorTest("this message was extracted from the MessageExtractorTest constructor.");

        try
        {
            throw new WrongUsageException("this message was extracted from the WrongUsageException constructor.");
        }
        catch (WrongUsageException e)
        {
            e.printStackTrace();
        }

        try
        {
            throw new MissingParameterException("This message won't be extracted.");
        }
        catch (MissingParameterException e)
        {
            e.printStackTrace();
        }
    }
}
