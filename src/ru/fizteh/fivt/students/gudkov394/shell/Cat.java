package ru.fizteh.fivt.students.gudkov394.shell;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
/*проверено, работает*/
public class Cat
{
    public Cat(String[] current_args, CurrentDirectory cd)
    {
        if (current_args.length > 2)
        {
            System.err.println("extra arguments for Cat");
            System.exit(1);
        }
        String read = "";
        File f = new File(cd.get_Current_directory(),current_args[1]);
        if(!f.exists())
        {
            System.err.println("File is not exists" + current_args[1]);
            System.exit(2);
        }
        try
        {
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine())
            {
                read = sc.nextLine();
                System.out.println(read);
            }
        }
        catch (FileNotFoundException e)
        {
            System.err.println("problem in cat with File ");
        }

    }
}
