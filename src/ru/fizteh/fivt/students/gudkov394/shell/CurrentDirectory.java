package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

public class CurrentDirectory
{
    private String current_directory;
    public CurrentDirectory()
    {
        current_directory = get_home();
    }

    public String get_Current_directory()
    {
        return current_directory;
    }

    public String get_home()
    {
        return(new File("").getAbsolutePath());
    }

    public void change_current_directory(String s)
    {
        current_directory = s;
    }
}
