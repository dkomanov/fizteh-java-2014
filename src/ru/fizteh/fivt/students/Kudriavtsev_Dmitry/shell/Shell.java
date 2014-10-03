package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.shell;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

//import  java.nio.channels.FileChannel;

public class Shell {
    //public static final String CHECKSTYLE_PREFIX;
    private static void remove(String whatDelete, String directory, boolean r) {
        File f = new File(directory + File.separator + whatDelete);
        if (!f.exists()) {
            System.err.println("rm: cannot remove \"" + f.getName() + "\": No such file or directory");
            System.exit(-1);
        }
        if (f.isFile()) {
            if (!f.delete()) {
                System.err.println("rm: cannot remove \"" + f.getName() + "\"");
                System.exit(-1);
            }
        }
        if (f.isDirectory()) {
            if (r) {
                String[] list;
                list = f.list();
                for (String value : list) {
                    remove(value, directory + File.separator + whatDelete, true);
                }
                if (!f.delete()) {
                    System.err.println("rm: cannot remove \"" + f.getName() + "\"");
                    System.exit(-1);
                }
            } else {
                System.err.println("rm: " + f.getName() + " is a directory");
                System.exit(-1);
            }
        }
    }

    private static void copy(File f1, File f2, boolean r) {
        try {
            if (f1.getCanonicalPath().equals(f2.getCanonicalPath())) {
                System.err.println("cp: can't copy file on the same file");
                System.exit(-1);
            }
            if (f1.isFile()) {
                if (!f2.exists()) {
                    if (!f2.mkdir()) {
                        System.err.println("mkdir: Cannot create new directory " + f2.getName());
                        System.exit(-1);
                    }
                }
                InputStream is = null;
                OutputStream os = null;
                try {
                    is = new FileInputStream(f1);
                    File dest;
                    if (f2.isFile()) {
                        dest = new File(f2.getCanonicalPath());
                    } else {
                        dest = new File(f2.getCanonicalPath() + File.separator + f1.getName());
                    }
                    os = new FileOutputStream(dest);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                } finally {
                    is.close();
                    os.close();
                }
                //Files.copy(f1.toPath(), f2.toPath(),);
            } else {
                if (r) {
                    if (!f2.exists()) {
                        if (!f2.mkdir()) {
                            System.err.println("mkdir: Cannot create new directory " + f2.getName());
                            System.exit(-1);
                        }
                    }
                    File newDir = new File(f2.getCanonicalPath() + File.separator + f1.getName());
                    Files.createDirectory(newDir.toPath());
                    String[] list = f1.list();
                    for (String aList : list) {
                        copy(new File(f1, aList), newDir, true);
                    }
                } else {
                    System.err.println("cp: " + f1.getName() + " is a directory (not copied).");
                    System.exit(-1);
                }
            }

        } catch (IOException e) {
            System.err.println("Exception on coping: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static String cd(String directory, String what) throws IOException {
        Path path = new File(directory).toPath();

        File newPath = path.resolve(what).toAbsolutePath().normalize().toFile();
        if (newPath.exists() && newPath.isDirectory()) {
            System.setProperty("user.dir", newPath.getCanonicalPath());
        } else if (!newPath.exists()) {
            System.err.println("path '" + newPath.toString() + "' doesn't exist");
            System.exit(-1);
        } else {
            System.err.println(what + " is not a directory");
            System.exit(-1);
        }
        directory = System.getProperty("user.dir");
        return directory;
    }

    private static void ls(String directory) {
        File path = new File(directory);
        if (path.isDirectory()) {
            String[] list;
            list = path.list();
            for (String value : list) {
                System.out.println(value);
            }
        } else {
            System.err.println("It is not a directory");
            System.exit(-1);
        }
    }

    private static void mkdir(String directory, String dirname) {
        File newDir = new File(directory + File.separator + dirname);
        if (newDir.exists()) {
            System.err.println("New directory exists");
            System.exit(-1);
        }
        try {
        Files.createDirectory(newDir.toPath());
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void mv(String directory, String source, String destination) {
        File f1 = new File(directory + File.separator + source);
        File f2 = new File(directory + File.separator + destination);
        if ((f1.isFile() && !f2.exists()
                && f1.getParent().equals(f2.getParent()))
                ||
                (f1.isDirectory() && !f2.exists()
                        && f1.getParent().equals(f2.getParent()))
                ) {
            if (!f1.renameTo(f2)) {
                System.out.println("mv: can't rename " + f1.getName());
                System.exit(-1);
            }
        } else {
            if (f1.isFile()) {
                copy(f1, f2, false);
                remove(f1.getName(), f1.getParent(), false);
            }
            if (f1.isDirectory()) {
                if (f2.isFile()) {
                    System.err.println("mv: can't move directory in file");
                    System.exit(-1);
                }
                copy(f1, f2, true);
                remove(f1.getName(), f1.getParent(), true);
            }
        }
    }

    private static void cat(String directory, String name) {
        try {
        File f = new File(directory + File.separator + name);
        if (!f.exists()) {
            System.err.println("cat: " + f.getName() + ": no such file");
            System.exit(-1);
        }
        if (f.isDirectory()) {
            System.err.println("cat: " + f.getName() + ": it is a directory");
            System.exit(-1);
        }
        BufferedReader fin = new BufferedReader(new FileReader(f));
        String line;

        while ((line = fin.readLine()) != null) {
            System.out.println(line);
        }
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static void pwd(String directory) {
        try {
        File f = new File(directory);
        System.out.println(f.getCanonicalPath());
        } catch (IOException e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
    }

    private static String whatDelete(String[] s, int i) {
        String whatDelete;
        int j = s[i].indexOf(';');
        if (j != -1) {
            whatDelete = s[i].substring(0, s[i].length() - 1);
        } else {
            whatDelete = s[i];
            if (i + 1 < s.length) {
                System.err.println("too much arguments");
                System.exit(-1);
            }
        }
        return whatDelete;
    }

    private static String destination(String[] s, int i) {
        String destination;
        int j = s[i].indexOf(';');
        if (j != -1) {
            destination = s[i].substring(0, s[i].length() - 1);
        } else {
            destination = s[i];
            if (i + 1 < s.length) {
                System.err.println("too much arguments");
                System.exit(-1);
            }
        }
        return destination;
    }

    public static void main(String[] args) {
        try {
            int i = 0;
            String directory = System.getProperty("user.home");
            String[] s;
            boolean pack = false;
            if (args.length == 0) {
                System.out.print("$ ");
                try {
                Scanner sc = new Scanner(System.in);
                String s1 = sc.nextLine();
                s = s1.split("\\s+");
                } catch (Exception e) {
                    System.err.println("Exception: " + e.getMessage());
                    System.exit(-1);
                    s = new String[1];
                    s[0] = "";
                }
            } else {
                pack = true;
                s = args;
            }
            while (s.length == 0) {
                System.out.print("$ ");
                try {
                    Scanner sc = new Scanner(System.in);
                    String s1 = sc.nextLine();
                    s = s1.split("\\s+");
                } catch (Exception e) {
                    System.err.println("Exception: " + e.getMessage());
                    System.exit(-1);
                    s = new String[1];
                    s[0] = "";
                }
            }
            while (!s[i].equals("exit")) {
                while (i < s.length && !s[i].equals("exit")) {
                    try {
                        switch (s[i]) {
                            case "cd": {
                                ++i;
                                String what;
                                int j = s[i].indexOf(';');
                                if (j != -1) {
                                    what = s[i].substring(0, s[i].length() - 1);
                                } else {
                                    what = s[i];
                                    if (i + 1 < s.length) {
                                        System.err.println("too much arguments");
                                        System.exit(-1);
                                    }
                                }
                                directory = cd(directory, what);
                                ++i;
                                break;
                            }
                            case "ls":
                            case "ls;": {
                                if (s[1].equals("ls")) {
                                    if (i + 1 < s.length) {
                                        System.err.println("too much arguments");
                                        System.exit(-1);
                                    }
                                }
                                if (directory == null) {
                                    System.err.println("Bad directory");
                                    System.exit(-1);
                                    ++i;
                                    break;
                                }
                                ls(directory);
                                ++i;
                                break;
                            }
                            case "mkdir": {
                                ++i;
                                String dirname;
                                int j = s[i].indexOf(';');
                                if (j != -1) {
                                    dirname = s[i].substring(0, s[i].length() - 1);
                                } else {
                                    dirname = s[i];
                                    if (i + 1 < s.length) {
                                        System.err.println("too much arguments");
                                        System.exit(-1);
                                    }
                                }
                                mkdir(directory, dirname);
                                ++i;
                                break;
                            }
                            case "pwd":
                            case "pwd;": {
                                if (s[1].equals("pwd")) {
                                    if (i + 1 < s.length) {
                                        System.err.println("too much arguments");
                                        System.exit(-1);
                                    }
                                }
                                pwd(directory);
                                ++i;
                                break;
                            }
                            case "mv": {
                                ++i;
                                String source = s[i];
                                ++i;
                                mv(directory, source, destination(s, i));
                                ++i;
                                break;
                            }
                            case "rm": {
                                ++i;
                                boolean flag = false;
                                if (s[i].equals("-r")) {
                                    flag = true;
                                    ++i;
                                }
                                remove(whatDelete(s, i), directory, flag);
                                ++i;
                                break;
                            }
                            case "cp": {
                                ++i;
                                boolean flag = false;
                                if (s[i].equals("-r")) {
                                    flag = true;
                                    ++i;
                                }
                                String destination;
                                String source = s[i];
                                ++i;
                                int j = s[i].indexOf(';');
                                if (j != -1) {
                                    destination = s[i].substring(0, s[i].length() - 1);
                                } else {
                                    destination = s[i];
                                    if (i + 1 < s.length) {
                                        System.err.println("too much arguments");
                                        System.exit(-1);
                                    }
                                }
                                File f1 = new File(directory + File.separator + source);
                                File f2 = new File(directory + File.separator + destination);
                                copy(f1, f2, flag);
                                ++i;
                                break;
                            }
                            case "cat": {
                                ++i;
                                String name;
                                while (s[i].equals("")) {
                                    ++i;
                                }
                                int j = s[i].indexOf(';');
                                if (j != -1) {
                                    name = s[i].substring(0, s[i].length() - 1);
                                } else {
                                    name = s[i];
                                    if (i + 1 < s.length) {
                                        System.err.println("too much arguments");
                                        System.exit(-1);
                                    }
                                }
                                cat(directory, name);
                                ++i;
                                break;
                            }
                            default: {
                                System.exit(-1);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Exception: " + e.getMessage());
                        System.exit(-1);
                    }
                }
                if (i < s.length) {
                    if (s[i].equals("exit")) {
                        break;
                    }
                }
                if (pack) {
                    return;
                }
                i = 0;
                System.out.print("$ ");
                Scanner sc = new Scanner(System.in);
                String s1 = sc.nextLine();
                s = s1.split(" ");
            }
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(-1);
        }
    }
}
