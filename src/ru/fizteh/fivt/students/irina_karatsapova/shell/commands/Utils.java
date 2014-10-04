package ru.fizteh.fivt.students.irina_karatsapova.shell.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {
    
    public static String concatStrings(String[] args, String separator) {
        StringBuilder sb = new StringBuilder();
        if (args.length == 0) {
            return "";
        }
        sb.append(args[0]);
        for (int i = 1; i < args.length; i++) {
            if (args[i] != null) {
                sb.append(separator).append(args[i]);
            }
        }
        return sb.toString();
    }
    
    public static String currentPath() {
        return System.getProperty("user.dir");
    }
    
    public static void newCurrentPath(String str) {
        System.setProperty("user.dir", str);
    }
    
    public static Path makePathAbsolute(String str) {
        Path path = Paths.get(str);
        if (!path.isAbsolute()) {
            path = Paths.get(Utils.currentPath(), path.toString());
        }
        return path.normalize();
    }
    
    public static void print(File file) throws Exception {
        try {
            BufferedReader fin = new BufferedReader(new FileReader(file));
            String line;
            while ((line = fin.readLine()) != null) {
                System.out.println(line);
            }
            fin.close();
        } catch (IOException e) {
            throw new Exception(file.toString() + ": Can't read");
        }
    }
    
    public static void copy(File copied, File destination) throws Exception {
        try {
            Files.copy(copied.toPath(), destination.toPath());
        } catch (IOException e) {
            throw new Exception(copied.toString() + ": Can't copy");
        }
    }
    
    public static void delete(File file) throws Exception {
        try {
            file.delete();
        } catch (Exception e) {
            throw new Exception(file.toString() + ": Can't delete");
        }
    }
    
    public static void checkExistance(File file) throws Exception {
        if (!file.exists())    {
            throw new Exception(file.toString() + ": No such file or directory");
        }
    }
    
    public static void checkNonExistance(File file) throws Exception {
        if (file.exists()) {
            throw new Exception(file.toString() + ": Already exists");
        }
    }
    
    public static void checkFile(File file) throws Exception {
        if (!file.isFile()) {
            throw new Exception(file.toString() + ": Not a file");
        }
    }
    
    public static void checkDirectory(File file) throws Exception {
        if (!file.isDirectory()) {
            throw new Exception(file.toString() + ": Not a directory");
        }
    }

    public static void checkSubDirectory(File source, File destination) throws Exception {
        if (destination.toString().startsWith(source.toString())) {
            throw new Exception(source.toString() + ": Subdirectory of " + destination.toString());
        }
    }

    public static void checkArgumentsNumber(int realNumber, int correctNumber) throws Exception {
        if (realNumber != correctNumber) {
            throw new Exception("Wrong number of arguments");
        }
    }
}


