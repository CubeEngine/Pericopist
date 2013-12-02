package test;

import test.anot.TestSingleMemberAnnotation;

public class Main
{
    public void main(String[] args)
    {
        I18n i18n = new I18n();

        i18n.sendTranslated("hello everyone");
        i18n.getTranslation("whats up?");
        i18n.getTranslation("whats up?");

        i18n.getTranslationN("hope %s is fine?", "hope you are fine", getOnlinePersons(), "Phillip");

        SecondTestclass second = new SecondTestclass(i18n, "hello");

        this.getNonTranslation("Bye bye!");
    }

    private String getNonTranslation(String string, Object ... o)
    {
        return String.format(string, o);
    }

    @TestSingleMemberAnnotation("annotationString")
    private int getOnlinePersons()
    {
        return 2;
    }
}
