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
import de.cubeisland.messageextractor.test.command.CommandSender;
import de.cubeisland.messageextractor.test.command.Console;
import de.cubeisland.messageextractor.test.command.User;
import de.cubeisland.messageextractor.test.exception.MissingParameterException;
import de.cubeisland.messageextractor.test.exception.WrongUsageException;
import de.cubeisland.messageextractor.test.i18n.DefaultI18n;
import de.cubeisland.messageextractor.test.i18n.ExtendedI18n;
import de.cubeisland.messageextractor.test.i18n.I18n;

public class MessageExtractorTest
{
    public static void main(String args[])
    {
        new MessageExtractorTest("This is the start of the messageextractor test");
    }

    public static final String TEST_STRING_CONST = "test_string_const";
    private static final int TEST_INT_CONST = 1;

    public MessageExtractorTest(String startMessage)
    {
        DefaultI18n i18n = new DefaultI18n();

        i18n.translate("welcome %s to the messageextractor test.", "YOUR_NAME");
        i18n.translate(TranslatableEnum.FIRST.toString() + "\nDoes that work?");

        i18n.translate("The main task of the project is to generate a message catalog containing all the string literals of a project by parsing its source tree.");
        i18n.translate("This is the core module of the messageextractor.");
        i18n.translate("One can extract messages from special methods, constructors and annotation fields");
        i18n.translate("The test is an example of how it works and shows examples.");

        i18n.translateN(0, "One monkey is better than sheep", "We've %d monkeys", 0);
    }

    @Command(desc = "extracted from command annotation, desc", usage = "command annotation usage")
    @CommandParameter("This message won't be extracted.")
    public void extendedI18nTest()
    {
        ExtendedI18n i18n = new ExtendedI18n();

        i18n.translate("A singular message extracted from the ExtendedI18n class");
        i18n.translateN(0, "One monkey is better than sheep", "We've %d monkeys", 0);
    }

    @Command(desc = "extracted from command annotation, desc", usage = "command annotation usage")
    @CommandParameter("This message won't be extracted.")
    public void userCommandSenderTest()
    {
        CommandSender sender = new User();

        sender.sendTranslated("This is a message extracted from a user method");
        sender.sendTranslated(TEST_STRING_CONST);
        sender.sendTranslated("test_int_const: " + TEST_INT_CONST);
        sender.sendTranslated("test_enum: " + TranslatableEnum.FIRST);

        sender.sendTranslatedN(0, "One monkey is better than sheep", "We've %d monkeys", 0);
    }

    @Command(desc = "extracted from command annotation, desc", usage = "command annotation usage")
    @CommandParameter("This message won't be extracted.")
    public void userCommandSenderTest2()
    {
        User sender = new User();

        sender.sendTranslated("This is a message extracted from a user method");
        sender.sendTranslated(TEST_STRING_CONST);
        sender.sendTranslated("test_int_const: " + TEST_INT_CONST);
        sender.sendTranslated("test_enum: " + TranslatableEnum.FIRST);

        sender.sendTranslatedN(0, "One monkey is better than sheep", "We've %d monkeys", 0);
    }

    @Command(desc = "extracted from command annotation, desc", usage = "command annotation usage")
    @CommandParameter("This message won't be extracted.")
    public void consoleCommandSenderTest()
    {
        CommandSender sender = new Console();

        sender.sendTranslated("This is a message extracted from a user method");
        sender.sendTranslated(TEST_STRING_CONST);
        sender.sendTranslated("test_int_const: " + TEST_INT_CONST);
        sender.sendTranslated("test_enum: " + TranslatableEnum.FIRST);

        sender.sendTranslatedN(0, "One monkey is better than sheep", "We've %d monkeys", 0);
    }

    @Command(desc = "extracted from command annotation, desc", usage = "command annotation usage")
    @CommandParameter("This message won't be extracted.")
    public void consoleCommandSenderTest2()
    {
        Console sender = new Console();

        sender.sendTranslated("This is a message extracted from a user method");
        sender.sendTranslated(TEST_STRING_CONST);
        sender.sendTranslated("test_int_const: " + TEST_INT_CONST);
        sender.sendTranslated("test_enum: " + TranslatableEnum.FIRST);

        sender.sendTranslatedN(0, "One monkey is better than sheep", "We've %d monkeys", 0);
    }

    @Command(desc = "extracted from command annotation, desc", usage = "command annotation usage")
    @CommandParameter("This message won't be extracted.")
    public void constructorTest()
    {
        try
        {
            throw new WrongUsageException("this message was extracted from a WrongUsageException.");
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