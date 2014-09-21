package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
/*работает*/

public class Copy
{
    private void copy_recursive(File from, File to)
    {
        try
        {
            CopyOption[] options = new CopyOption[]{ StandardCopyOption.REPLACE_EXISTING};
            Files.copy(from.toPath(), to.toPath(), options);
        }
        catch (IOException e2)
        {
            System.err.println("problem with copy");
            System.exit(3);
        }

        if (from.isDirectory())
        {
                try
                {
                    for (File f : from.listFiles())
                    {
                        File new_to = new File(to.getAbsolutePath(), f.getName());
                        copy_recursive(f, new_to);
                    }
                }
                catch (NullPointerException e1)
                {
                    System.err.println("Sorry, problem with ListFiles in copy_recursive");
                    System.exit(3);
                }
        }
    }

    public Copy(String[] current_args, CurrentDirectory cd)
    {
        if(current_args.length > 4)
        {
            System.out.println("more than 4 arguments to cp");
            System.exit(1);
        }
        File from = new File(current_args[1]);
        File to = new File(current_args[2]);
        if(!from.isAbsolute())
        {
            from = new File(cd.get_Current_directory(), current_args[1]);
        }
        if(!to.isAbsolute())
        {
            to = new File(cd.get_Current_directory(), current_args[2]);
        }
        if(current_args.length == 3)
        {
            if(from.isFile() && to.isDirectory())
            {
                try
                {
                    File new_to = new File(to.getAbsolutePath(), from.getName());
                    Files.copy(from.toPath(), new_to.toPath());
                }
                catch (IOException e2)
                {
                    System.err.println("problem with copy");
                    System.exit(3);
                }
            }
            else
            if(from.isDirectory() && to.isDirectory())
            {
                File new_to = new File(to.getAbsolutePath(), from.getName());
                try
                {
                    CopyOption[] options = new CopyOption[]{ StandardCopyOption.REPLACE_EXISTING};
                    Files.copy(from.toPath(), new_to.toPath(), options);
                }
                catch (IOException e2)
                {
                    System.err.println("problem with copy");
                    System.exit(3);
                }
            }
            else
            {
                System.err.println("fail with copy directory");
                System.exit(2);
            }
        }
        else
        if(current_args.length == 4 && current_args[1].equals("-r"))
        {
            if(from.isDirectory() && to.isDirectory())
            {
                File new_to = new File(to.getAbsolutePath(), from.getName());
                try
                {
                    CopyOption[] options = new CopyOption[]{ StandardCopyOption.REPLACE_EXISTING};
                    Files.copy(from.toPath(), new_to.toPath(), options);
                }
                catch (IOException e2)
                {
                    System.err.println("problem with copy");
                    System.exit(3);
                }
                copy_recursive(from, new_to);
            }
            else
            {
                System.err.println("fail with copy directory maybe -r is excess");
                System.exit(2);
            }
        }
        else
        {
            System.err.println("wrong argument to copy");
        }
    }
}
