package ru.fizteh.fivt.students.gudkov394.shell;

import java.util.Scanner;

public class Shell
{
    public Boolean check_name(String name)
    {
        String[] s = {"ls", "pwd", "cd", "mkdir", "rm", "cp", "mv", "cat", "exit"};
        for(int i = 0; i < s.length; ++i)
        {
            if(name.equals(s[i]))
            {
               return true;
            }
        }
        return  false;
    }

    public void run( String[] current_args, CurrentDirectory cd)
    {
        if(current_args[0].equals("pwd"))
        {
            Pwd pwd = new Pwd(current_args, cd);
        }
        else
        if(current_args[0].equals("mkdir"))
        {
            Mkdir mkdir = new Mkdir(current_args, cd);
        }
        else
        if(current_args[0].equals("cd"))
        {
            Change_directory change_directory = new Change_directory(current_args, cd);
        }
        else
        if(current_args[0].equals("rm"))
        {
            Remove_derictory remove_derictory = new Remove_derictory(current_args, cd);
        }
        else
        if(current_args[0].equals("cp"))
        {
            Copy copy = new Copy(current_args, cd);
        }
        else
        if(current_args[0].equals("mv"))
        {
            MoveFile moveFile = new MoveFile(current_args, cd);
        }
        else
        if(current_args[0].equals("ls"))
        {
            Ls ls = new Ls(current_args, cd);
        }
        else
        if(current_args[0].equals("exit"))
        {
            Exit exit = new Exit(current_args);
        }
        else
        if(current_args[0].equals("cat"))
        {
            Cat cat = new Cat(current_args, cd);

        }
        else
        {
            System.err.println("wrong command");
            //System.exit(228);
        }
    }

    public void interactive()
    {
        CurrentDirectory current_Directory = new CurrentDirectory();
        Scanner sc = new Scanner(System.in);
        while(true)
        {
            System.out.print(current_Directory.get_Current_directory() + "$");
            String current_string = sc.nextLine();
            String[] functions = current_string.split(";");
            for(int i = 0; i < functions.length; ++i)
            {
                run(functions[i].split(" "), current_Directory);
            }
        }

    }

    public  void package_mode(String[] args)
    {
        CurrentDirectory current_Directory = new CurrentDirectory();
        int i = 0;
        while(i < args.length)
        {
            int first = i;
            ++i;
            while(i < args.length && !check_name(args[i]))
            {
                ++i;
            }
            String[] s = new String[i - first];
            for(int j = 0; j < s.length; ++j)
            {
                s[j] = args[j + first];
            }
            run(s, current_Directory);
        }
    }


    public Shell(String[] args)
    {
         if(args.length == 0)
         {
             interactive();
         }
         else
         {
             package_mode(args);
         }
    }
}
