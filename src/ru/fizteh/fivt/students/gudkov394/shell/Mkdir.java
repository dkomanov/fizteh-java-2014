package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

public class Mkdir
{
    public Mkdir(String[] current_args, CurrentDirectory cd)
    {
        if(current_args.length > 2)
        {
            System.err.println("extra arguments for mkdir");
            System.exit(1);
        }
        if(current_args.length > 2)
        {
            System.err.println("I need name for directory");
            System.exit(1);

        }
         File f = new File(cd.get_Current_directory(), current_args[1]);
         if(!f.mkdirs())
         {
             System.err.println("I can't create the directory");
         }
    }
}
