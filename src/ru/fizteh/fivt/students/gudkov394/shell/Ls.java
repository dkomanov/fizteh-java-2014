package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

public class Ls
{
    public Ls(String[] current_args, CurrentDirectory cd)
    {
        if(current_args.length > 1)
        {
            System.err.println("extra arguments for ls");
            System.exit(1);
        }
        File f = new File(cd.get_Current_directory());
        try
        {
            for (File tmp : f.listFiles())
            {
                System.out.println(tmp.getName());
            }
        }
        catch (NullPointerException e2)
        {
            System.err.println("problem with listFiles in Ls");
            System.exit(2);
        }
    }
}
