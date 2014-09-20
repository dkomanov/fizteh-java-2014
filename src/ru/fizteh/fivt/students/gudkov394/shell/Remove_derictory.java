package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;

public class Remove_derictory
{
    private void recursive_delete(File f)
    {
        if(!f.isDirectory())
        {
            f.delete();
        }
        else
        {
            try
            {
                for (File tmp : f.listFiles())
                {
                    recursive_delete(tmp);
                }
            }
            catch (NullPointerException e2)
            {
                System.err.println("Sorry, problem with ListFiles in delete_recursive");
                System.exit(3);
            }
        }
    }

    public Remove_derictory(String[] current_args, CurrentDirectory cd)
    {
         if(current_args.length > 3)
         {
             System.err.println("more then 3 arguments to rm");
             System.exit(1);
         }
         else
         if(current_args.length == 2)
         {
             File f = new File(cd.get_Current_directory() + current_args[1]);
             f.delete();
         }
         else
         if(current_args[1].equals("-r"))
         {
             File f = new File(cd.get_Current_directory() + current_args[2]);
             recursive_delete(f);
         }
         else
         {
             System.err.println("fig arguments");
             System.exit(2);
         }

    }
}
