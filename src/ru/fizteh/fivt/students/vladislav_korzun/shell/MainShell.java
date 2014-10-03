package ru.fizteh.fivt.students.vladislav_korzun.shell;

import java.util.Scanner;
import java.util.regex.PatternSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public final class MainShell {
    private MainShell() {
        
    }
    
    public static void main(final String[] args) {
        Scanner in = new Scanner(System.in);
        InterfaceShell interfaceshell = new InterfaceShell();
        File dir = new File(System.getProperty("user.dir"));
        String request = new String();    
        String path = new String();
        String destanation = new String();        
        Parser parser = new Parser();
        String[] arg = null;
        String[] arg2 = null;    
        int i = 0;
        do {            
            if (args.length == 0) {
                System.out.print("$ ");
                request = in.nextLine();
            } else {
                for (i = 0; i < args.length; i++) {
                    request += args[i];
                }                
                request = request + ";exit";
            }            
            arg = parser.pars(request);            
            for (i = 0; i < arg.length; i++) {
                arg2 = parser.pars2(arg[i]);
                switch(arg2[0]) {                
                    case "ls":     
                        interfaceshell.ls(dir);
                        break;
                    case "cd":    
                        path = arg2[1];    
                        dir = interfaceshell.cd(dir, path);                        
                        break;
                    case "mkdir":
                        path = arg2[1];
                        interfaceshell.mkdir(dir, path);
                        break;
                    case "pwd": 
                        interfaceshell.pwd(dir);
                        break;
                    case "rm":
                        path = arg2[1];
                        if (!path.equals("-r")) {
                            interfaceshell.rm(dir, path, false);
                        } else {
                            path = arg2[2];
                            interfaceshell.rm(dir, path, true);
                        }
                        break;
                    case "cp":
                        path = arg2[1];
                        destanation = arg2[2];
                        interfaceshell.cp(dir, path, destanation);
                        break;
                    case "mv":
                        path = arg2[1];
                        destanation = arg2[2];
                        interfaceshell.mv(dir, path, destanation);
                        break;
                    case "cat":
                        path = arg2[1];
                        interfaceshell.cat(dir, path);
                        break;
                    case "exit":
                        in.close();
                        interfaceshell.exit();
                        break;
                    default:
                        System.out.println("Invalid command");
                        if (args.length > 0) {
                            in.close();
                            System.exit(1);
                        }                        
                        break;
            }    }        
        } while(arg2[0] != "exit");            
    }
}

class InterfaceShell {
    void ls(final File dir) {
        try {
            if (dir.isDirectory()) {                        
                File[] fls = dir.listFiles();
                for (int i = 0; i < fls.length; i++) {
                    System.out.println(fls[i].getName());   
                }                
            }                
        } catch (SecurityException e) {
            System.out.println("Something get wrong");        
        }        
    }    
    
    File cd(final File dir, final String path) {            
        try {
            if (!Paths.get(path).isAbsolute()) {
                File dirbuf = new File(dir.getCanonicalPath() 
                        + File.separator + path);
                 if (dirbuf.isDirectory()) {    
                    System.out.println(dirbuf.getCanonicalPath());
                    return dirbuf;
                }
            } else {
                File dirbuf = new File(path);
                if (dirbuf.isDirectory()) {    
                    System.out.println(dirbuf.getCanonicalPath());
                    return dirbuf;
                }
            }
        } catch (SecurityException e) {
             System.out.println("No such directory!");
        } catch (IOException e) {
            System.out.println("I/O error");
        }
        System.out.println("No such directory!");        
        return dir;                 
    }    
    
    void mkdir(final File dir, final String path) {
        try {
            if (!Paths.get(path).isAbsolute()) {
                File dirbuf = new File(dir.getCanonicalPath() 
                        + File.separator + path);
                if (!dirbuf.mkdir()) {
                    System.out.println("File already exists");        
                }
            } else {
                File dirbuf = new File(path);
                if (!dirbuf.mkdir()) {
                    System.out.println("File already exists");
                }
            }
        } catch (SecurityException e) {
             System.out.println("No such directory!");
        } catch (IOException e) {
            System.out.println("I/O error");
        }            
    }
    
    void pwd(final File dir) {
        try {
            System.out.println(dir.getCanonicalPath());
        } catch (IOException e) {    
            System.out.println("I/O error: canonical pathname"
                    + " may require filesystem queries");
        }        
    }
    
    void exit()    {
        try {
            System.exit(0);
        } catch (SecurityException e) {
            System.out.println("Can't exit");
        }
    }    
    void rm(final File dir, final String path, final boolean key) {        
        try {        
            if (!Paths.get(path).isAbsolute()) {
                File dirbuf = new File(dir.getCanonicalPath() 
                        + File.separator + path);
                if (dirbuf.isFile()) {
                    if (!dirbuf.delete()) {                    
                        System.out.println("No such file");    
                    }
                } else if (dirbuf.isDirectory()) {
                    if (key) {
                        InterfaceShell interfaceshell 
                            = new InterfaceShell();
                        File[] fls = dirbuf.listFiles();
                        for (int i = 0; i < fls.length; i++) {
                            interfaceshell.rm(dirbuf, 
                                    fls[i].getName(), true);
                        }                            
                        if (!dirbuf.delete()) {            
                            System.out.println("No such file");    
                        }                            
                    } else {
                        System.out.println("This is directory!");    
                    }
                } else {
                        System.out.println("No such file");
                    }
            } else {
                File dirbuf = new File(path);
                if (dirbuf.isFile()) {
                    if (!dirbuf.delete()) {            
                            System.out.println("No such file");
                    }
                } else if (dirbuf.isDirectory()) {
                    if (key) {
                        InterfaceShell interfaceshell = new InterfaceShell();
                        File[] fls = dirbuf.listFiles();
                        for (int i = 0; i < fls.length; i++) {
                            interfaceshell.rm(dirbuf, fls[i].getName(), true);
                        }  
                        if (!dirbuf.delete()) {            
                                System.out.println("No such file");
                        }
                    } else {
                        System.out.println("This is directory!");    
                    }
                } else {
                        System.out.println("No such file");
                }
            }
        } catch (SecurityException e) {
             System.out.println("No such directory or file");
        } catch (IOException e) {
            System.out.println("I/O error");
        }            
    }
    
    boolean cp(final File dir, final String path, final String destanation)    {
        try    {
            File file;
            File file2;            
            if (dir.isDirectory()) {
                if (!Paths.get(path).isAbsolute()) {
                    file = new File(dir.getCanonicalPath() 
                            + File.separator + path);
                } else {
                    file = new File(path);
                }
                if (!Paths.get(destanation).isAbsolute()) {
                    file2 = new File(dir.getCanonicalPath() 
                            + File.separator + destanation);
                } else {
                    file2 = new File(destanation);
                }    
                if (file2.isDirectory()) {
                    file2 = new File(file2.getCanonicalPath() 
                            + File.separator + file.getName());
                }
                if (file.isFile()) {        
                    FileReader input = new FileReader(file);
                    FileWriter output = new FileWriter(file2);
                    int ch;                
                    while ((ch = input.read()) > 0) {                    
                        output.write(ch);
                    }
                    input.close();
                    output.close();
                    } else {
                        System.out.println("No such file");
                        return false;
                    }
            } else {
                System.out.println("Fatal Error");
                return false;
            }            
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return false;
        } catch (IOException e) {
            System.out.println("I/O error");
            return false;
        } catch (SecurityException e) {
            System.out.println("Something get wrong");
            return false;
        }
        return true;
    }
    void mv(final File dir, final String path, final String destanation) {
        if (cp(dir, path, destanation)) {
            rm(dir, path, true);
        }
    }
    
    void cat(final File dir, final String path)    {
        try    {
            if (dir.isDirectory()) {
                if (!Paths.get(path).isAbsolute()) {
                    File file = new File(dir.getCanonicalPath() 
                            + File.separator + path);
                    if (file.isFile()) {
                        FileReader input 
                        = new FileReader(file);                    
                        int ch;                
                        while ((ch = input.read()) > 0) {                    
                            System.out.print((char) ch);
                        }
                        input.close();                    
                    } else {
                        System.out.println("No such file");
                    }
                } else {
                    File file = new File(path);
                    if (file.isFile()) {
                        FileReader input 
                        = new FileReader(file);                    
                        int ch;                
                        while ((ch = input.read()) > 0) {                    
                            System.out.print((char) ch);
                        }
                        input.close();
                    } else {
                        System.out.println("No such file");
                    }
                }
            } else {
                System.out.println("Fatal Error");
            }            
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("I/O error");
        } catch (SecurityException e) {
            System.out.println("Something get wrong");
        }
    }
}

class Parser {
    String[] pars(final String arg) {        
        String[] answer = null;
        String buffer = new String();
        try {
        buffer = arg.trim();    
        answer = buffer.split(";");
        } catch (PatternSyntaxException e) {
            System.out.println("Ivalid command");
        }
        return answer;
        
    }
    String[] pars2(final String arg) {
        String[] answer = null;
        String buffer = new String();
        try {
            buffer = arg.trim();
            answer = buffer.split(" ");
        } catch (PatternSyntaxException e) {
            System.out.println("Ivalid command");
        }
        return answer;
    }
}
