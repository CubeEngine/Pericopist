package de.cubeisland.maven.plugins.messagecatalog.format.gettext;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

import de.cubeisland.maven.plugins.messagecatalog.format.CatalogFormat;
import de.cubeisland.maven.plugins.messagecatalog.parser.Occurrence;
import de.cubeisland.maven.plugins.messagecatalog.parser.TranslatableMessage;

import org.apache.maven.plugin.logging.Log;

public class PlaintextGettextCatalogFormat implements CatalogFormat
{
    private final Map<String, Object> config;
    private final Log log;

    public PlaintextGettextCatalogFormat(Map<String, Object> config, Log log)
    {
        this.config = config;
        this.log = log;
    }

    public void write(File file, Set<TranslatableMessage> messages) throws IOException
    {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));

        try
        {
            writeHeader(writer);
            writer.write("\n");

            for (TranslatableMessage message : messages)
            {
                for (Occurrence occurrence : message.getOccurrences())
                {
                    writer.write("#: " + occurrence.getFile().getPath() + ":" + occurrence.getLine() + "\n");
                }
                writeMessageId(writer, message.getMessage());
                writeMessageString(writer);
                writer.write("\n");
            }
            writer.flush();
        }
        finally
        {
            writer.close();
        }
    }

    private static void writeHeader(BufferedWriter writer) throws IOException
    {
        writeMessageId(writer, "");
        writeMessageString(writer);
        writeHeaderLine(writer, "Project-Id-Version: <PROJECT>");
        writeHeaderLine(writer, "POT-Creation-Date: <DATE>");
        writeHeaderLine(writer, "PO-Revision-Date: <DATE>");
        writeHeaderLine(writer, "Last-Translator: <NAME>");
        writeHeaderLine(writer, "Language-Team: <TEAM>");
        writeHeaderLine(writer, "Language: <LANGUAGE>");
        writeHeaderLine(writer, "MIME-Revision: 1.0");
        writeHeaderLine(writer, "Content-Type: text/plain; charset=UTF-8");
        writeHeaderLine(writer, "Content-Transfer-Encoding: 8bit");
        writeHeaderLine(writer, "X-Generator: maven-messagecatalog-plugin");
    }

    private static void writeHeaderLine(Writer writer, String content) throws IOException
    {
        writer.write("\"" + content + "\\n\"\n");
    }

    private static void writeMessageId(Writer writer, String msgid) throws IOException
    {
        writer.write("msgid \"" + msgid + "\"\n");
    }

    private static void writeMessageString(Writer writer) throws IOException
    {
        writer.write("msgstr \"\"\n");
    }

    public String getFileExtension()
    {
        return "po";
    }
}
