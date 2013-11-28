package test;

import static test.I18n._;
import test.anot.TestNormalAnnotation;

public class SecondTestclass
{
    public SecondTestclass(I18n i18n, String hello)
    {
        i18n.getTranslation("what's" + " up" + "?");

        i18n.getTranslation("goodbye");
    }

    @TestNormalAnnotation(desc = "normalAnnotString")
    public void test()
    {
        _("hello everyone");
    }
}
