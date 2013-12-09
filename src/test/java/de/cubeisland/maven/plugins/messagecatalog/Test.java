package de.cubeisland.maven.plugins.messagecatalog;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.cubeisland.maven.plugins.messagecatalog.message.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessage;
import de.cubeisland.maven.plugins.messagecatalog.message.TranslatableMessageManager;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParser;
import de.cubeisland.maven.plugins.messagecatalog.parser.SourceParserFactory;
import de.cubeisland.maven.plugins.messagecatalog.parser.UnknownSourceLanguageException;
import de.cubeisland.maven.plugins.messagecatalog.parser.java.translatables.TranslatableAnnotation;
import de.cubeisland.maven.plugins.messagecatalog.util.Misc;

public class Test
{
    public static void main(String[] args)
    {
        TranslatableAnnotation annotation = new TranslatableAnnotation("de.cubeengine.Command:desc,usage");
        System.out.println("FQN: " + annotation.getFullQualifiedName());
        System.out.println("SimpleName " + annotation.getSimpleName());
        System.out.println("Fields: " + annotation.getFields());

        try
        {
            TranslatableMessageManager messageManager = new TranslatableMessageManager();
            messageManager.addMessage("pre-added", null, 1);
            messageManager.addMessage("2nd pre-added string", "pre-added string has a plural!", 2);

            Map<String, Object> options = new HashMap<String, Object>();
            options.put("methods", "getTranslation sendTranslated:1 _ getTranslationN:1,2 sendTranslationN:1,2");
            options.put("annotations", "test.anot.TestNormalAnnotation:desc,usage test.anot.TestSingleMemberAnnotation");
            options.put("message_manager", messageManager);

            SourceParser parser = SourceParserFactory.newSourceParser("java", options, new Logger());

            File file = new File("D:\\Programmieren\\Java\\messagecatalog-maven-plugin\\src\\test\\resources");
            for(TranslatableMessage message : parser.parse(file))
            {
                for(Occurrence occurrence : message.getOccurrences())
                {
                    System.out.println("# " + Misc.getNormalizedRelativePath(file, occurrence.getFile()));
                }
                System.out.println("msgid = \"" + message.getSingular() + "\"");
                if (message.hasPlural())
                {
                    System.out.println("msgid_plural = \"" + message.getPlural() + "\"");
                }
                System.out.println();
            }
        }
        catch (UnknownSourceLanguageException e)
        {
            e.printStackTrace();
        }
    }
}
