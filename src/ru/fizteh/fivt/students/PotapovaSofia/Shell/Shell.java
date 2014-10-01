package ru.fizteh.fivt.students.sofia_potapova.shell;

import java.io.File;
import java.io.FileReader;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Shell {
    private static String currentPath = System.getProperty("user.dir");

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            interMode();
        } else {
            packMode(args);
        }
    }
    public static void interMode() throws Exception {
        System.out.println("$ ");
        try {
            Scanner in = new Scanner(System.in);
            while (in.hasNextLine()) {
                String str = in.nextLine();
                String[] cmds = str.split(";");
                for (String cmd : cmds) {
                    try {
                        parseIMode(cmd);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                }
                System.out.println("$ ");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println();
    }
    public static void packMode(String[] args) throws Exception {
        String[] cmds = parsePMode(args);
        for (String cmd : cmds) {
            try {
                parseIMode(cmd);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
    public static void parseIMode(String str) throws Exception {
        String []cmdt = str.split(" ");
        String []cmdr = new String [cmdt.length];
        int k = 0;
        for (String st : cmdt) {
            if (!st.equals("")) {
                cmdr[k] = st;
                k += 1;
            }
        }
        commandParse(cmdr, k);
    }
    public static String[] parsePMode(String[] args) {
        String cmd;
        StringBuilder newArgs = new StringBuilder();
        for (String str : args) {
            newArgs.append(str).append(' ');
        }
        cmd = newArgs.toString();
        return cmd.split(";");
    }
    public static void commandParse(String []runningCmd, int len) throws Exception {
        if (runningCmd[0].equals("cd")) {
            try {
                if (len > 2) {
                    throw new Exception("cd: too much arguments\n");
                } else if (len < 2) {
                    throw new Exception("cd: few arguements\n");
                } else {
                    changeDir(runningCmd[1]);
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: cd <dirname>");
            }
        } else if (runningCmd[0].equals("mkdir")) {
            if (len > 2) {
                throw new Exception("mkdir: too much arguments\n");
            } else if (len < 2) {
                throw new Exception("mkdir: few arguements\n");
            } else {
                makeDir(runningCmd[1]);
            }
        } else if (runningCmd[0].equals("pwd")) {
            if (len > 1) {
                throw new Exception("pwd: too much arguments\n");
            } else {
                printWorkDir();
            }
        } else if (runningCmd[0].equals("rm")) {
            try {
                if (len < 2) {
                    throw new Exception("rm: few arguments\n");
                } else if (len == 2) {
                    remove(runningCmd[1], false);
                } else {
                    if (len == 3 && runningCmd[1].equals("-r")) {
                        remove(runningCmd[2], true);
                    } else {
                        throw new Exception("rm: too much arguments\n");
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: rm [-r] <filename>");
            }

        } else if (runningCmd[0].equals("cp")) {
            try {
                if (len < 3) {
                    throw new Exception("cp: few  arguments\n");
                } else if (len == 3) {
                    copy(runningCmd[1], runningCmd[2], false);
                } else if (len == 4 && runningCmd[1].equals("-r")) {
                    copy(runningCmd[2], runningCmd[3], true);
                } else {
                    throw new Exception("cp: too much arguments\n");
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: cd <dirname>");
            }
        } else if (runningCmd[0].equals("mv")) {
            try {
                if (len < 3) {
                    throw new Exception("mv: few arguments\n");
                } else if (len > 3) {
                    throw new Exception("mv: too much arguments\n");
                } else {
                    move(runningCmd[1], runningCmd[2]);
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: mv [-r] <source> <destination>");
            }
        } else if (runningCmd[0].equals("ls")) {
            if (len > 1) {
                throw new Exception("ls: too much argument\n");
            }
            ls();
        } else if (runningCmd[0].equals("cat")) {
            try {
                if (len > 2) {
                    throw new Exception("cat: too much arguments\n");
                } else if (len < 2) {
                    throw new Exception("cat: few arguments\n");
                } else {
                    cat(runningCmd[1]);
                }
            } catch (IndexOutOfBoundsException e) {
                throw new Exception("usage: mkdir <dirname>");
            }
        } else if (runningCmd[0].equals("exit")) {
            if (len > 1) {
                throw new Exception("exit: too much arguments");
            }
            System.exit(0);
        } else {
            throw new Exception(runningCmd[0] + " not found\n");
        }
    }
    public static String pathSplit(String path) {
        String []splitted = path.split("/");
        boolean fromRoot = path.startsWith("/");
        int backSteps = 0;
        int finishIndex = 0;
        String []handledPath = new String[splitted.length];
        for (String st : splitted) {
            if (st.equals("..")) {
                if (finishIndex > 0) {
                    finishIndex--;
                } else {
                    backSteps++;
                }
            } else if (!st.equals(".")) {
                handledPath[finishIndex] = st;
                finishIndex++;
            }
        }
        if (fromRoot) {
            String res = "";
            for (int i = 0; i < finishIndex; i++) {
                if (i != finishIndex - 1) {
                    res += handledPath[i] + "/";
                } else {
                    res += handledPath[i];
                }
            }
            return res;
        }

        String[] prefix = currentPath.split("/");
        String res = "";
        if (backSteps >= prefix.length) {
            for (int i = 0; i < finishIndex; i++) {
                if (i != finishIndex - 1) {
                    res += handledPath[i] + "/";
                } else {
                    res += handledPath[i];
                }
            }
        } else {
            for (int i = 0; i < prefix.length - backSteps; i++) {
                if (i != prefix.length - backSteps - 1) {
                    res += prefix[i] + "/";
                } else {
                    res += prefix[i];
                }
            }
            if (finishIndex != 0) {
                res += "/";
            }
            for (int i = 0; i < finishIndex; i++) {
                if (i != finishIndex - 1) {
                    res += handledPath[i] + "/";
                } else {
                    res += handledPath[i];
                }
            }
        }

        if (!res.startsWith("/")) {
            res = "/" + res;
        }
        return res;
    }
    public static void changeDir(String str) throws Exception {
        String path = pathSplit(str);
        File dir = new File(path);
        if (!dir.exists()) {
            throw new Exception(str + ": No such file or directory\n");
        }
        if (!dir.isDirectory()) {
            throw new Exception(str + ": Not a directory\n");
        }
        currentPath = path;
    }
    public static void makeDir(String str) throws Exception {
        String path = pathSplit(str);
        File dir = new File(path);
     /*   if (dir.exists()) {
            throw new Exception("mkdir: " + str + ": Directory is already exists\n");
        }
        if (!dir.mkdir()) {
            throw new Exception("mkdir: " + str + ": Can't create directory\n");
        }
       */
        try {
            Files.createDirectory(dir.toPath());
        } catch (FileAlreadyExistsException e) {
            throw new Exception(str + ": Already exists");
        }
    }
    public static void printWorkDir() throws Exception {
        System.out.println(currentPath + "\n");
    }
    public static void remove(String str, boolean key) throws Exception {
        String path = pathSplit(str);
        File dir = new File(path);
        if (!dir.exists()) {
            throw new Exception("rm: " + str + ": Not exists\n");
        }
        if (dir.isDirectory()) {
            if (!key) {
                throw new Exception("rm: " + str + " is a directory (use -r)\n");
            } else {
                if (dir.listFiles().length == 0) {
                    if (!dir.delete()) {
                        throw new Exception("rm: " + str + ": Can't delete\n");
                    }
                } else {
                    if (!recRm(dir)) {
                        throw new Exception("rm: " + str + ": Can't delete\n");
                    }
                }
            }
        } else {
            if (!dir.delete()) {
                throw new Exception("rm: " + str + ": Can't delete\n");
            }
        }
    }
    public static boolean recRm(File dir)throws Exception {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new Exception("rm: No such file or directory\n");
        }
        for (File f : files) {
            if (f.isDirectory()) {
                if (f.listFiles().length != 0) {
                    if (!recRm(f)) {
                        return false;
                    }
                }
            } else {
                if (!f.delete()) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
    public static void copy(String source, String destination, boolean key) throws Exception {
        String sourcePath = pathSplit(source);
        File sourceDir = new File(sourcePath);
        String destPath = pathSplit(destination);
        File destDir = new File(destPath);
        if (sourceDir.equals(destDir)) {
            throw new Exception("cp: " + source + " and " + destination + " are the same\n");
        }
        if (!sourceDir.exists()) {
            throw new Exception("cp: " + source + ": No such file or directory\n");
        }
        if (sourceDir.isDirectory() && !key) {
            throw new Exception("cp: " + source + ": is Directory (use -r)\n");
        }
        if (destDir.isDirectory() && destDir.exists()) {
            destDir = new File(destDir, source);
        }
        if (!destDir.exists()) {
            if (sourceDir.isDirectory() && !destDir.mkdir()) {
                throw new Exception("cp: " + destination + ": Can't create\n");
            }
            if (sourceDir.isFile() && !destDir.createNewFile()) {
                throw new Exception("cp: " + destination + ": Can't create\n");
            }
        }
        recCopy(sourceDir, destDir);
    }
    public static void recCopy(File source, File dest) throws Exception {
        if (source.isFile()) {
            copyFile(source, dest);
        } else {
            copyDir(source, dest);
        }
    }
    public static void copyFile(File source, File dest) throws Exception {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } catch (Exception e) {
            throw new Exception("cp: " + source + "Can't read file\n");
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }
    }
    public static void copyDir(File source, File dest) throws Exception {
        dest.mkdir();
        String[] files = source.list();
        for (String file : files) {
            recCopy(new File(source, file), new File(dest, file));
        }
    }
    public static void move(String source, String destination) throws Exception {
        String sourcePath = pathSplit(source);
        File sourceDir = new File(sourcePath);
        String destPath = pathSplit(destination);
        File destDir = new File(destPath);
        if ((sourceDir.exists()) && (sourceDir.getPath().equals(destDir.getPath()))) {
            return;
        }
        if ((sourceDir.exists()) && (sourceDir.isDirectory()) && (!destDir.exists()) && (sourceDir.getParent().
                equals(destDir.getParent()))) {
            if (!sourceDir.renameTo(destDir)) {
                throw new Exception("rm: " + source + "' : No such file or  directory");
            }
            return;
        }
        copy(source, destination, true);
        remove(source, true);
    }
    public static void ls() throws Exception {
        File fl = new File(currentPath);
        System.out.println(currentPath + ":\n");
        String[] files = fl.list();
        for (String st : files) {
            System.out.println(st + "\n");
        }
    }
    public static void cat(String str) throws Exception {
        String path = pathSplit(str);
        File f = new File(path);
        if (!f.exists()) {
            throw new Exception("cat: " + str + ": No such file\n");
        }
        if (!f.canRead()) {
            throw new Exception("cat: " + str + ": Can't read file\n");
        }
        FileReader fr = new FileReader(f);
        int c = fr.read();
        while (c > 0) {
            System.out.print((char) c);
            c = fr.read();
        }
        fr.close();
    }
}






