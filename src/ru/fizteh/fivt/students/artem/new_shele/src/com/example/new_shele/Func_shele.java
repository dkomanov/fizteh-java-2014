package com.example.new_shele;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.nio.file.StandardCopyOption;


public class Func_shele {
    public String currentDirectory;

    public void mkdir(String[] args) {
        if (args.length == 2) {
            File f = new File(currentDirectory + File.separator + args[1]);
            if (!f.exists()) {
                try {
                    if (!f.mkdir()) System.out.println("Folder isn't create");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
    public void ls() throws IOException {
        File f = new File(currentDirectory);
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }



    public void cat(String[] args) throws IOException {
        if (args.length == 2) {
            File f = new File(currentDirectory + File.separator + args[1]);
            if(f.exists()) {
                BufferedReader br = null;
                try {
                    br = new BufferedReader((new FileReader(f)));
                    String line = "test";
                    while (line != null) {
                        try {
                            line = br.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (line != null)
                            System.out.println(line);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else System.out.println("cat: '"+f.getName()+"': No such file or directory");
        } else System.out.println("Incorrect arguments");
    }

    public void delete_file_directories(File f) throws IOException {
        if(f.isDirectory()) {
           System.out.println(f.getName());
            File[] files = f.listFiles();
            if(files!=null) {
                delete_file_directories(f);
            }
            else return;
        }

        if(!f.delete()) System.out.println("not perform to delete");
   }
    public void rm(String[] args) throws IOException {
        if ((args.length == 3 && args[1].equals("-r")) || (args.length == 2)) {
            File f;
            if (args.length == 3) f = new File(currentDirectory + File.separator + args[2]);
            else f = new File(currentDirectory + File.separator + args[1]);
            if (f.exists()) {
                if (f.isDirectory()) {
                        if (args.length == 3)
                            delete_file_directories(f);
                        else System.out.println("rm: "+f.getName()+": is a directory");
                } else if (!f.delete()) System.out.println("not perform to delete");
            } else  System.out.println("rm: cannot remove '"+f.getName()+"': No such file or directory");
        } else System.out.println("Incorrect arguments");
        }


    public void recursiveCopy(File f1, File f2) throws IOException {
        File d = null;
        if(f2.isDirectory())
        d = new File(f2.getCanonicalPath()+ File.separator + f1.getName());
        else return;
        Files.copy(f1.toPath(),d.toPath(),StandardCopyOption.COPY_ATTRIBUTES);
        if(f2.isDirectory()) {
            File[] files = f1.listFiles();
            if(files!=null) {
                for(File f:files)
                    recursiveCopy(f,d);
            }
        }
    }

    public void cp(String[] args) throws IOException {
        if ((args.length == 4 && args[1].equals("-r")) || (args.length == 3)) {
            File f1, f2;
            f1 = new File(currentDirectory + File.separator + args[args.length - 2]);
            f2 = new File(currentDirectory + File.separator + args[args.length - 1]);
            if (Files.exists(f1.toPath()) && Files.exists(f2.toPath())) {
                if(!f2.toPath().startsWith(f1.toPath())&&!Files.exists(Paths.get(f2.toString(),f1.getName()))) {
                    if (args.length == 4) {
                        recursiveCopy(f1, f2);
                    } else {
                        if (f1.isDirectory())
                            System.out.println("cp: " + f1.getName() + " is a directory (not copied).");
                    }
                }
                else System.out.println("File is already exist");
            } else {
                if (!Files.exists(f1.toPath()))
                    System.out.println("cp: cannot copied '" + args[args.length - 2] + "': No such file or directory ");
                if (!Files.exists(f2.toPath()))
                    System.out.println("cp: cannot copied to '" + args[args.length - 1] + "': No such file or directory ");
            }
        } else System.out.println("Incorrect arguments");
    }

    public void mv(String[] args) throws IOException {
        if (args.length == 3) {
            File f1 = new File(currentDirectory + File.separator + args[args.length - 2]);
            File f2 = new File(currentDirectory + File.separator + args[args.length - 1]);
            if(f1.exists()&&f2.exists()) {
                if(f1.toString().equals(f2.toString())) System.out.println("files is identify");
                else {
                    if(!f2.toPath().startsWith(f1.toPath())) {
                        recursiveCopy(f1,f2);
                        delete_file_directories(f1);
                    }
                    else System.out.println("unable to move");
                }
            }
            else {
                if(!f1.exists()) System.out.println("mv: cannot move '" + args[args.length - 2] + "': No such file or directory ");
                if(!f2.exists()) System.out.println("mv: cannot move '" + args[args.length - 1] + "': No such file or directory ");
            }
        } else System.out.println("Incorrect arguments");
    }

    public void cd(String[] args) {
        if (args.length == 2) {
            File current;
            if (args[1].charAt(0) == '/') {
                current = new File(args[1]);
            } else {
                current = new File(currentDirectory
                        + File.separator + args[1]);
            }
            try {
                if (current.exists() && current.isDirectory()) {
                    System.setProperty("user.dir", current.getCanonicalPath());
                    currentDirectory = System.getProperty("user.dir");
                } else {
                    throw new IOException();
                }
            } catch (IOException e) {
                e.getMessage();
            }
        } else System.out.println("Incorrect arguments");
    }

    public void pwd(String[] args) {
        if (args.length == 1)
            System.out.println(currentDirectory);
        else System.out.println("Incorrect arguments");
    }


    public void exit() throws ShellexecException {
        throw new ShellexecException();

    }

    public void ShellSwitch(String com) throws ShellexecException, IOException {
        com = com.trim();
        String[] s = com.split("\\s+");
        try {
            if (s[0].equals("cd")) {
                cd(s);
            }
            else if (s[0].equals("pwd")) {
                pwd(s);

            } else if (s[0].equals("ls")) {
                ls();

            } else if (s[0].equals("cat")) {
                cat(s);

            } else if (s[0].equals("mkdir")) {
                mkdir(s);

            } else if (s[0].equals("rm")) {
                rm(s);

            } else if (s[0].equals("cp")) {
                cp(s);

            } else if (s[0].equals("mv")) {
                mv(s);

            } else if (s[0].equals("exit")) {
                exit();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }
    public void SwitchCommands(String[] s) {
        try {
            for (String command : s)
                    ShellSwitch(command);

        }catch (ShellexecException e) {
            System.exit(0);
        }
        catch (IOException e) {
            System.exit(0);
        }
    }
    public void parsLine(String[] args) {
        StringBuilder com = new StringBuilder();
        for (String arg: args) {
            com.append(arg);
            com.append(' ');
        }
        switchLine(com.toString());
    }
    public void switchLine(String line) {
        String[] commands = line.trim().split(";");
        SwitchCommands(commands);
    }

    public void interactive() {
        try {
            Scanner scan = new Scanner(System.in);
            while (true) {
                System.out.print("$ ");
                String commands = scan.nextLine();
                switchLine(commands);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

}
class ShellexecException extends Exception {}


