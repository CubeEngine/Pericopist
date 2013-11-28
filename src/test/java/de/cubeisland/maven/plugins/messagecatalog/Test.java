package de.cubeisland.maven.plugins.messagecatalog;

import java.io.File;
import java.util.HashMap;

import de.cubeisland.maven.plugins.messagecatalog.parser.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParserFactory;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.parser.UnknownSourceLanguageException;

public class Test
{
    public static void main(String[] args)
    {
        try
        {
            SourceParser parser = SourceParserFactory.newSourceParser("java", new HashMap<String, Object>(1), new Logger());
            parser.setTranslatableMethodNames(new String[]{"getTranslation", "sendTranslated", "_"});

            File file = new File("D:\\Programmieren\\Java\\messagecatalog-maven-plugin\\src\\test\\resources");
            for(TranslatableMessage message : parser.parse(file))
            {
                for(Occurrence occurrence : message.getOccurrences())
                {
                    System.out.println("#" + occurrence.getFile().getPath() + ":" + occurrence.getLine());
                }
                System.out.println(message.getMessage());
                System.out.println();
            }

        }
        catch (UnknownSourceLanguageException e)
        {
            e.printStackTrace();
        }
    }
}
