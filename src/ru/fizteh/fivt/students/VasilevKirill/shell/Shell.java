package ru.fizteh.fivt.students.VasilevKirill.shell;

import java.io.*;

/**
 * Created by Vasilev Kirill on 22.09.2014.
 */
public class Shell {
    public static void main(String[] args){
        if (args.length == 0){
            new Shell().handle(System.in);
        }
        else{
            new Shell().handle(args);
        }
    }

    private void handle(InputStream stream){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String command = "";
            while (!command.equals("exit")) {
                System.out.print("$ ");
                command = reader.readLine();
                String[] cmds = command.split("\\s+");

                switch (cmds[0]) {
                    case "cd":
                        if (cd(cmds) == -1) System.err.println("Something very bad happened");
                        break;
                    case "mkdir":
                        if (mkdir(cmds) == -1) System.err.println("Something very bad happened");
                        break;
                    case "pwd":
                        pwd();
                        break;
                    case "rm":
                        if (rm(cmds,0,currentPath) == -1) System.err.println("Something very bad happened");
                        break;

                }
            }
        }
        catch (IOException e)
        {
            System.err.println("IOException caught");
        }
    }

    private void handle(String[] args){

    }

    private int cd (String[] args)
    {
        String path = currentPath;
        if (args.length < 2) return 0;
        else if (args[1].equals(".")) return 0;
        else if (args[1].equals("..")) {
            if (path.equals("C:")) return 0;
            int lastIndex = path.lastIndexOf("\\");
            if (lastIndex < 0) return -1;
            currentPath = path.substring(0,lastIndex);
        }
        else
        {
            if (args[1].substring(0,2).equals("C:"))
            {
                File directory = new File(args[1]);
                if (!directory.exists() || !directory.isDirectory())
                {
                    System.out.println("cd: '" + args[1] + "': No such file or directory");
                    return 1;
                }
                currentPath = directory.getPath();
            }
            else
            {
                File directory = new File(currentPath + File.pathSeparator + args[1]);
                if (!directory.exists() || !directory.isDirectory())
                {
                    System.out.println("cd: '" + args[1] + "': No such file or directory");
                    return 1;
                }
                currentPath = currentPath + File.pathSeparator + args[1];
            }
        }
        return 0;
    }

    private int mkdir(String[] args)
    {
        if (args.length < 2) return 0;
        if (args[1] == null) return 0;
        File directory = new File(currentPath + File.pathSeparator + args[1]);
        if (!directory.mkdirs()) return 1;
        return 0;
    }

    private int pwd()
    {
        System.out.println(currentPath);
        return 0;
    }

    private int rm(String[] args, int start, String directory)
    {
        if (args.length < 2) return 0;
        if (args[1] == null) return 0;
        if (args[1].equals("-r")){
            File file = new File(directory + File.pathSeparator + args[2]);
            if (!file.exists()){
                if (start == 0) System.out.println("rm: cannot remove '" + args[2] + "': No such file or directory\"");
                return 1;
            }
            if (file.isDirectory()){
                File[] listFiles = file.listFiles();
                if (listFiles.length == 0)
                    if (!file.delete()) return -1;
                    else;
                else{
                    for (File f : listFiles){
                        String[] new_args = {"rm","-r",f.getName()};
                        rm(new_args,1,directory + File.pathSeparator + file.getName());
                    }
                }
                if (!file.delete()) return -1;
            }
            else{
                if (!file.delete()) return -1;
            }
        }
        else {
            File file = new File(currentPath + File.pathSeparator + args[1]);
            if (!file.exists()){
                System.out.println("rm: cannot remove '" + args[1] + "': No such file or directory");
                return 1;
            }
            if (file.isDirectory()){
                System.out.println("rm: " + args[1] + ": is a directory");
                return 1;
            }
            if (!file.delete()) return -1;
        }
        return 0;
    }

    private static String currentPath = "C:";
}
