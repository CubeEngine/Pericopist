package test;

import test.anot.TestNormalAnnotation;
import test.anot.TestSingleMemberAnnotation;

public class TranslatableClass
{
    public TranslatableClass()
    {
        String test1 = TranslatableClass.getTranslation("hello");

        getTranslation("whats up %s?");

        String test2 = getTranslation("hello" + "you");
    }

    @TestNormalAnnotation(desc = "normalAnnotString")
    @TestSingleMemberAnnotation("annotationString")
    public static String getTranslation(String string)
    {
        return null;
    }

    public void test()
    {
        System.out.println(getTranslation("hello"));
    }
}
