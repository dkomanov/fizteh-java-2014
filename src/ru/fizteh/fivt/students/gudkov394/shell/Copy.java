package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.*;

public class Copy
{
    private void copy_file(File from, File to)
    {
        FileInputStream input = null;
        FileOutputStream output = null;
        try
        {
            input = new FileInputStream(from);
            output = new FileOutputStream(to);
            byte[] buffer = new byte[1000];
            int flag = 0;
            try
            {
                while ((flag = input.read(buffer)) > 0)
                {
                    output.write(buffer, 0, flag);
                }
            }
            catch (IOException e)
            {
                System.err.println("problem in copy with reading");
                System.exit(1);
            }

        }
        catch (FileNotFoundException e)
        {
            System.err.println("Problem with streams in cp");
            System.exit(1);
        }
        finally
        {
            try
            {
                input.close();
                output.close();
            }
            catch (IOException e)
            {
                System.err.println("close file is failed in copy");
                System.exit(1);
            }
            catch (NullPointerException e1)
            {
                System.err.println("I can't close stream. sorry");
                System.exit(3);
            }
        }
    }

    private void copy_recursive(File from, File to)
    {
        if (from.isDirectory())
        {
            try
            {
                File fromNew = new File(to.getCanonicalPath(), from.getName());
                if (!fromNew.mkdirs())
                {
                     throw new IOException("Unable to create this directory - " + fromNew.getCanonicalPath());
                }
                try
                {
                    for (File f : from.listFiles()) {
                        copy_recursive(f, fromNew);
                    }
                }
                catch (NullPointerException e1)
                {
                    System.err.println("Sorry, problem with ListFiles in copy_recursive");
                    System.exit(3);
                }
            }
            catch (IOException e)
            {
                System.err.println("problem with getCanonicalPath in copy_recursive");
                System.exit(1);
            }
        }
        try
        {
            to = new File(to.getCanonicalPath() + File.separator + from.getName());
            if (!to.exists()) {
                to.createNewFile();
            }
        }
        catch (IOException e)
        {
            System.err.println("problem in copy recursive");
            System.exit(1);
        }
        copy_file(from, to);
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
            if (from.isFile() && to.isFile())
            {
                copy_file(from, to);
            }
            else
            if(from.isFile() && to.isDirectory())
            {
                File tmp = new File(cd.get_home() + cd.get_Current_directory() + from.getName());  // не забудь проверить, что эта хрень работает
                copy_file(tmp, to);
            }
            else
            if(from.isDirectory())
            {
                copy_recursive(from, to);
            }
        }
    }
}
