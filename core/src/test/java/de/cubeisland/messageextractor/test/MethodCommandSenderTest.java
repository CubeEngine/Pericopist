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
package de.cubeisland.messageextractor.test;

import de.cubeisland.messageextractor.test.command.CommandSender;
import de.cubeisland.messageextractor.test.command.Console;
import de.cubeisland.messageextractor.test.command.User;

public class MethodCommandSenderTest
{
    public void testUser()
    {
        User user = new User();

        user.sendTranslated("extracted from User class");
        user.sendTranslatedN(0, "singular of a plural message from User class", "plural of the plural message from User class");
    }

    public void testConsole()
    {
        Console console = new Console();

        console.sendTranslated("this message won't be extracted!");
        console.sendTranslatedN(0, "this singular message won't be extracted!", "this plural message won't be extracted!");
    }

    /**
     * this won't be extracted because polymorphism isn't supported!
     * The messageextractor thinks that the class is CommandSender, but that class isn't registered.
     */
    public void testUserPolymorphism()
    {
        CommandSender sender = new User();

        sender.sendTranslated("this message won't be extracted!");
        sender.sendTranslatedN(0, "this singular message won't be extracted!", "this plural message won't be extracted!");
    }
}
