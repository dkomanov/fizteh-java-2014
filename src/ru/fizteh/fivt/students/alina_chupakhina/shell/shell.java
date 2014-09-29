package ru.fizteh.fivt.students.alina_chupakhina.shell;

import java.io.File;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Shell {

    static private String current;
    static private boolean out;

    public static void main(String[] args){
        current = System.getProperty("user.dir");
        try {
            if (args.length > 0) {
                conapp(args);
            } else {
                interactive();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void interactive(){
        out = false;
        Scanner sc = new Scanner(System.in);
        try {
            while (!out) {
                System.out.print("$ ");
                String s = sc.nextLine();
                docom(s);
            }
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void conapp (String[] args) {
        String arg;
        arg = args[0];
        for (int i = 1; i != args.length; i++) arg = arg + ' ' + args[i];
        String[] commands = arg.trim().split(";");
        try {
            for (int i = 0; i != commands.length; i++) {
                docom(commands[i]);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        interactive ();
    }

    public static void docom(String command) throws Exception {
        String[] args = command.trim().split("\\s+");
        try {
            if (args[0].equals("pwd")) {
                pwd();

            } else if (args[0].equals("cd")) {
                cd(args);

            } else if (args[0].equals("ls")) {
                ls(args);

            } else if (args[0].equals("cat")) {
                cat(args);

            } else if (args[0].equals("mkdir")) {
                makedir(args);

            } else if (args[0].equals("rm")) {
                rm(args);

            } else if (args[0].equals("cp")) {
                cp(args);

            } else if (args[0].equals("mv")) {
                mv(args);

            } else if (args[0].equals("exit")) {
                exit();

            } else if (args[0].equals("")) {
            } else {
                throw new Exception(args[0] + "Invalid command");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void mv(String[] args) throws Exception {
        if (args.length != 3){
            throw new Exception("cp: Incorrect number of arguments");
        }
        File source = new File(current + File.separator + args[args.length - 2]);
        File destination = new File(current + File.separator + args[args.length - 1]);
        if (source.getParent().equals(destination.getParent()) && !destination.exists()) {
            source.renameTo(destination);
            return;
        }
        if (!destination.exists() || !source.exists())
            throw new Exception("cp: File or Directory not found");
        if (destination.isFile())
            throw new Exception("cp: Destination is file");
        copy(source, destination);
        remove(source);
    }

    private static void exit() {
        out = true;
    }

    private static void copy (File source, File destination) throws Exception {
        String to = destination.getCanonicalPath() + File.separator + source.getName();
        destination = new File(to);
        if (source.isDirectory()) {
            if (!destination.exists())
                if (!destination.mkdir())
                    throw new Exception("cp: Can not create directory");
            File [] children = source.listFiles();
            if (!children.equals(null)) {
                for (int i = 0; i != children.length; i++) {
                    copy(children[i], destination);
                }
            }
        } else {
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
        }

    }

    private static void cp(String[] args) throws Exception {
        if (4 < args.length || args.length < 3){
            throw new Exception("cp: Incorrect number of arguments");
        }
        boolean rec = false;
        if (args.length == 4){
            if (!args[1].equals("-r")) {
                throw new Exception("cp: Incorrect number of arguments");
            } else
                rec = true;
        }
        File source = new File(current + File.separator + args[args.length - 2]);
        File destination = new File(current + File.separator + args[args.length - 1]);
        if (!source.exists()) {
            throw new Exception("cp: File or Directory not found");
        }
        if (!destination.exists()) {
            destination.mkdir();
        }
        if (destination.isFile()) {
            throw new Exception("cp: Destination is file");
        }
        if ((destination.getAbsolutePath().contains(source.getAbsolutePath()))) {
            throw new Exception("cp: Сan not be copied");
        }
        if (source.isDirectory() && !rec) {
            throw new Exception("cp: Unable to copy directory not recursive");
        }
        copy(source, destination);
    }

    private static void rm(String[] args) throws Exception {
        boolean rec;
        if (args.length < 2 || args.length > 3){
            throw new Exception("rm: Incorrect number of arguments");
        }
        if (args[1].equals("-r") && args.length == 3){
            rec = true;
        } else {
            if (args[1].equals("-r") && args.length == 2){
                throw new Exception("rm: Incorrect function call");
            } else {
                rec = false;
            }
        }
        File file = new File(current+ File.separator + args[args.length - 1]);
        if (!file.exists()) {
            throw new Exception("rm: " + file.getAbsolutePath() + " No such file or directory");
        }
        if (file.isDirectory() && !rec) {
            throw new Exception("rm: " + file.getAbsolutePath() + " It's directory");
        }
        remove(file);
    }

    public static void remove(File file) throws Exception{
        if (file.isDirectory()) {
            File [] children = file.listFiles();
            for (int i = 0; i != children.length; i++) {
                    remove(children[i]);
            }
            file.delete();
        } else if (!file.delete())
            throw new Exception(
                "rm:" + file.getName() + ". Can not delete the file");
    }

    public static void pwd() {
        System.out.println(current);
    }

    public static void makedir(String[] args) throws Exception{
        String name = args[1];
        String path = current + File.separator + name;
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            throw new Exception("mkdir: Directory already exist");
        }
        if (!dir.mkdir()) {
            throw new Exception("mkdir: Can not create directory");
        }

    }

    public static void cd(String[] args) {
        String path = args[1];
        for (int i = 2; i != args.length; i++) {
            path = path + ' ' + args[i];
        }
        File dir = new File(path);
        if (!path.equals(dir.getAbsolutePath())) {
            path = current + File.separator + path;
            dir = new File (path);
        }
        if (!dir.exists()) {
            System.out.println("cd: '" + path + " No such file or directory");
        } else {
            current = path;
        }
    }

    public static void cat(String[] args) throws Exception {
        File f = new File(args[1]);
        if (!f.exists()) {
            throw new Exception("cat: File not exist");
        }
        BufferedReader fin = new BufferedReader(new FileReader(f));
        String line;
        try {
            while ((line = fin.readLine()) != null) System.out.println(line);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void ls(String[] args) throws Exception{
        if (args.length == 1) {
            File dir = new File(current);
            File[] children = dir.listFiles();
            int i;
            i = 0;
            while (i < children.length) {
                System.out.println(children[i].getName());
                i++;
            }
        } else {
            File dir = new File(args[1]);
            if (dir.isFile()) {
                throw new Exception("ls: It is not directory");
            }
            File[] children = dir.listFiles();
            int i;
            i = 0;
            while (i < children.length) {
                System.out.println(children[i].getName());
                i++;
            }
        }
    }
}
