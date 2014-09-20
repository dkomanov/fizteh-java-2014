package ru.fizteh.fivt.students.gudkov394.shell;


import java.io.File;
import java.io.IOException;

public class Change_directory
{
    public Change_directory(String[] current_args, CurrentDirectory cd)
    {
        if(current_args.length > 2)
        {
            System.err.println("extra arguments for cd");
            System.exit(1);
        }
        if(current_args.length == 1)
        {
            cd.change_current_directory(cd.get_home());
        }
        else
        {
            if (current_args[1].equals("."))
            {

            }
            else
            if(current_args[1].equals(".."))
            {
                File f = new File(cd.get_Current_directory());
                cd.change_current_directory(f.getParent());
            }
            else
            {
                File f = new File(current_args[1]);
                if(!f.isAbsolute())
                {
                    f = new File(cd.get_Current_directory(), current_args[1]);
                }
                try
                {
                    cd.change_current_directory(f.getCanonicalPath());
                }
                catch (IOException e)
                {
                    System.err.println("problem with directory");
                }
             }
        }
    }
}
