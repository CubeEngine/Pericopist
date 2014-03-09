package de.cubeisland.maven.plugins.messageextractor;

import org.apache.maven.plugin.logging.Log;

public class Logger implements Log
{
    public boolean isDebugEnabled()
    {
        return false;
    }

    public void debug(CharSequence content)
    {
        System.out.println(content);
    }

    public void debug(CharSequence content, Throwable error)
    {
        System.out.println(content);
        error.printStackTrace();
    }

    public void debug(Throwable error)
    {
        error.printStackTrace();
    }

    public boolean isInfoEnabled()
    {
        return false;
    }

    public void info(CharSequence content)
    {
        System.out.println(content);
    }

    public void info(CharSequence content, Throwable error)
    {
        System.out.println(content);
        error.printStackTrace();
    }

    public void info(Throwable error)
    {
        error.printStackTrace();
    }

    public boolean isWarnEnabled()
    {
        return false;
    }

    public void warn(CharSequence content)
    {
        System.out.println(content);
    }

    public void warn(CharSequence content, Throwable error)
    {
        System.out.println(content);
        error.printStackTrace();
    }

    public void warn(Throwable error)
    {
        error.printStackTrace();
    }

    public boolean isErrorEnabled()
    {
        return false;
    }

    public void error(CharSequence content)
    {
        System.out.println(content);
    }

    public void error(CharSequence content, Throwable error)
    {
        System.out.println(content);
        error.printStackTrace();
    }

    public void error(Throwable error)
    {
        error.printStackTrace();
    }
}
