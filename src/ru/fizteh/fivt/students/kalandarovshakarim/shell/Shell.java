/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
/**
 *
 * @author shakarim
 */
public class Shell {
    
    private static String curPath() {
        return System.getProperty("user.dir");
    }
    
    public static void pwd() {
        System.out.println(curPath());
    }
    
    public static void ls() {
        File curDir = new File(curPath());
        String[] list = curDir.list();
        
        for (String name : list) {
            if (name.charAt(0) != '.')
                System.out.println(name);
        }
    }
    
    public static void mkdir(File dir) {
        boolean isCreated = false;
        try {
            isCreated = dir.mkdir();
        } catch (Exception e) {
        }
        if (isCreated == false) {
            System.out.println("mkdir: Cannot create directory '"
                    + dir.getName() +"'");
        }
    }
    
    public static void cd(File dirToGo) {

        if (dirToGo.isDirectory() == true) {
            String newPath;
            try {
                newPath = dirToGo.getCanonicalPath();
                System.setProperty("user.dir", newPath);
            } catch (Exception e) {
            }
        } else {
            System.out.println("cd: '" 
                    + dirToGo.getName() + "' No such File or Directory");
        }
    }
    
    private static void readWrite(InputStream in, OutputStream out) {
        int lengthRead;
        byte[] buffer = new byte[4096];
        try {
            while ((lengthRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, lengthRead);
            }
        } catch (Exception e) {
        }
    }
    
    public static void cat(File file) {
        if (file.exists() == true) {
            InputStream in;
            try {
                in = new FileInputStream(file);
                readWrite(in, System.out);
            } catch(Exception e) {
            }
        } else {
            System.out.println("cat: '" 
                    + file.getName() + "' No such File or Directory");
        }
    }
    
    private static void copyFile(File source, File target) {
        if (source.exists() == true) {
            try {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target);
                readWrite(in, out);
            } catch(Exception e) {
                System.out.println("cp: Cannot create File '"
                        + target.getName() + "'");
            }
        } else {
            System.out.println("cp: '" 
                    + source.getName() + "' No such File or Directory");
        }
    }
    
    private static void copyDirectory(File source, File target) {
        if (target.mkdir() == true) {
            String[] files = source.list();
        
            for (String file : files) {
                cp(true, new File(source, file), new File(target, file));
            }
        } else  if (target.exists() == true) {
            copyDirectory(source, new File(target, source.getName()));
        } else {
            System.out.println("cp: Cannot create Directory '"
                    + target.getName() + "' No such File or Directory");
        }
    }
    
    public static void cp(boolean rec, File source, File target) {
        if (source.isDirectory() == true) {
            if (target.isFile() == true) {
                System.out.println("cp: '"
                        + source.getName() + "' is a Directory but '"
                        + target.getName() + "' is a File");
            } else if (rec == true) {
                copyDirectory(source, target);
            } else {
                System.out.println("cp: '" 
                        + source.getName() + "' is a Directory");
            }
        } else if (source.isFile() == true && target.isDirectory() == true) {
            copyFile(source, new File(target, source.getName()));
        } else if (source.exists() == true) {
            copyFile(source, target);
        } else {
            System.out.println("cp: '" 
                        + source.getName() + "' No such File or Directory");
        }
    }
    
    private static void rm(boolean rec, File file) {
        if (file.isFile() == true) {
            file.delete();
        } else if (rec == true && file.isDirectory() == true) {
            String[] list = file.list();
            for (String son : list) {
                rm(rec, new File(file, son));
            }
            file.delete();
        } else if (rec == false && file.isDirectory() == true) {
            System.out.println("rm: '" 
                    + file.getName() + "' is Directory");
        } else {
            System.out.println("rm: '" 
                    + file.getName() + "' No such File or Directory");
        }
    }
    
    private static void mv(File source, File target) {
        if (source.exists() == true) {
            cp(true, source, target);
            rm(true, source);
        } else {
            System.out.println("mv: '" 
                    + source.getName() + "' No such File or Directory");
        }
    }
    
    private static String readStr(InputStream in) {
        byte[] buff = new byte[4096];
        int length = 0;
        
        try {
            length = System.in.read(buff);
        } catch (Exception e) {
        }
        
        String retVal = new String(buff, 0, length - 1);

        return retVal;
    }
    
    private static String[] parseCmd(String command){
        String[] retVal = new String[4];
        int j = 0;
        
        for (int i = 0; i < 4; ++i) {
            StringBuilder cmd = new StringBuilder();
            while (j < command.length() && command.charAt(j) != ' ') {
                cmd.append(command.charAt(j));
                ++j;
            }
            while (j < command.length() && command.charAt(j) == ' ') {
               ++j;
            }
            retVal[i] = cmd.toString();
        }
        
        return retVal;
    }
    
    private static boolean caseCmd(String[] command) {
        File[] files = new File[3];
        
        for (int i = 0; i < 3; ++i) {
            if (command[i + 1].length() > 0) {
                if (command[i + 1].charAt(0) == '/') {
                    files[i] = new File(command[i + 1]);
                } else {
                    files[i] = new File(curPath() + "/" + command[i + 1]);
                }
            }
        }
        
        if (command[0].equals("exit") == true) {
            return false;
        } else if (command[0].equals("ls") == true) {
            ls();
        } else if (command[0].equals("pwd") == true) {
            pwd();
        } else if (command[0].equals("mkdir") == true) {
            mkdir(files[0]);
        } else if (command[0].equals("cd") == true) {
            cd(files[0]);
        } else if (command[0].equals("cat") == true) {
            cat(files[0]);
        } else if (command[0].equals("rm") == true) {
            if (command[1].equals("-r") == true) {
                rm(true, files[1]);
            }
            else { 
                rm(false, files[0]);
            }
        } else if (command[0].equals("cp") == true) {
            if (command[1].equals("-r") == true) {
                cp(true, files[1], files[2]);
            } else { 
                cp(false, files[0], files[1]);
            }
        } else if (command[0].equals("mv") == true) {
            mv(files[0], files[1]);
        } else if (command[0].length() != 0) {
            System.out.println("Shell: '" + command[0] + "' Unknown command");
        }
        
        return true;
    }
    
    public static void procShell() {
        String[] parsed;
        String command;
        do {
            System.out.print("$ ");
            command = readStr(System.in);
            parsed = parseCmd(command);
        } while (caseCmd(parsed) == true);
    }
    
    public static void procArgs(String[] args) {
        StringBuilder cmd = new StringBuilder();
        String[] parsed;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals(";") == true || i == args.length - 1) {
                if (i == args.length - 1) {
                    cmd.append(args[i]);
                }
                parsed = parseCmd(cmd.toString());
                caseCmd(parsed);
                cmd = new StringBuilder();
            } else {
                cmd.append(args[i]).append(' ');
            }
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            procShell();
        } else {
            procArgs(args);
        }
    }
}
