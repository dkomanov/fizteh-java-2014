package ru.fizteh.fivt.students.olga_chupakhina.shell;

        import java.io.IOException;
        import java.util.Scanner;
        import java.io.File;
        import java.io.BufferedReader;
        import java.io.FileReader;
        import java.nio.file.Files;
        import java.nio.file.StandardCopyOption;
        import java.util.NoSuchElementException;

public class Shell {

    private static String current;
    private static boolean mode;

    public static void main(String[] args) {
        try {
            current = System.getProperty("user.dir");
            if (args.length > 0) {
                mode = true;
                packageMode(args);
            }
            else {
                mode = false;
                interactiveMode();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void packageMode(String[] args) {
        StringBuilder commands = new StringBuilder();
        for (String arg: args) {
            commands.append(arg);
            commands.append(' ');
        }
        separationLine(commands.toString());
    }

    private static void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        try  {
            while (true) {
                System.out.print("$ ");
                String commands = scanner.nextLine();
                separationLine(commands);
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void separationLine(String line) {
        String[] commands = line.trim().split(";");
        try {
            for (int i = 0; i < commands.length; i ++) {
                DoCommand(commands[i]);
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }

    private static void DoCommand(String command)
            throws Exception {
        command = command.trim();
        String[] args = command.split("\\s+");
        boolean done = false;
        try {
            if (args[0].equals("cd")) {
                done = true;
                if (args.length != 2) {
                    throw new Exception(
                            "cd: Invalid number of arguments");
                }
                File file;
                if (args[1].charAt(0) == '/') {
                    file = new File(args[1]);
                } else {
                    file = new File(current + File.separator + args[1]);
                }
                    if (file.exists()) {
                        if (file.isDirectory()) {
                            System.setProperty("user.dir", file.getCanonicalPath());
                            current = System.getProperty("user.dir");
                        } else {
                            throw new Exception(
                                    );
                        }
                    }
                    else{
                    throw new Exception(
                            "cd: '" + args[1] + "': No such file or directory");
                    }
            }

            if (args[0].equals("mkdir")) {
                done = true;
                if (args.length != 2) {
                    throw new Exception(
                            "mkdir: Invalid number of arguments");
                }
                File newDirectory = new File(current + '/' + args[1]);
                if (newDirectory.exists()) {
                    throw new Exception(
                            "mkdir: " + args[1] + ": File already exists");
                } else if (!newDirectory.mkdir()) {
                    throw new Exception(
                            "mkdir: Unable to create file or directory");
                }
            }

            if (args[0].equals("pwd")) {
                done = true;
                if (args.length != 1) {
                    throw new Exception(
                            "mkdir: Invalid number of arguments");
                }
                System.out.println(current);
            }

            if (args[0].equals("rm")){
                done = true;
                if ((args.length > 3) && (args.length < 2)) {
                    throw new Exception(
                            "rm: Invalid number of arguments");
                }
                boolean r = false;
                File file = new File(
                        current + File.separator + args[args.length - 1]);
                if (args.length == 3) {
                    r = true;
                    if (!args[1].equals("-r")) {
                        throw new Exception(
                                "rm: Invalid argument");
                    }
                }
                if (!file.exists()) {
                    throw new Exception(
                            "rm: cannot remove" + args[args.length - 1] + ": No such file or directory");
                }
                if (file.isDirectory() && !r) {
                    throw new Exception(
                            "rm: Unable to create file or directory");
                }
                remove(file);
            }
            if (args[0].equals("cp")){
                done = true;
                if ((args.length > 4) && (args.length < 3)) {
                    throw new Exception(
                            "cp: Invalid number of arguments");
                }
                boolean r = false;
                if (args.length == 4) {
                    if (!args[1].equals("-r")) {
                        throw new Exception(
                                "cp: Invalid argument");
                    }
                    r = true;
                }
                File from = new File(current
                        + File.separator + args[args.length - 2]);
                File to = new File(current
                        + File.separator + args[args.length - 1]);
                if (!from.exists()) {
                    throw new Exception(
                            "cp: " + args[args.length - 2] + ": No such file or directory");
                }
                if (from.isDirectory() && !r) {
                    throw new Exception(
                            "cp: " + from.getName() + ": is a directory");
                }
                copy(from, to);
            }
            if (args[0].equals("mv ")){
                done = true;
                if ((args.length != 3)) {
                    throw new Exception(
                            "mv: Invalid number of arguments");
                }
                File from = new File(current
                        + File.separator + args[args.length - 2]);
                File to = new File(current
                        + File.separator + args[args.length - 1]);
                if (!from.exists()) {
                    throw new Exception(
                            "mv: " + from.getName() + ": No such file or directory");
                }
                if (from.equals(to)) {
                    return;
                }
                try {
                    copy(from, to);
                } catch (Exception e) {
                    String msg = e.getMessage();
                    msg = msg.replaceFirst("cp", "mv");
                    throw new Exception(msg);
                }
                try {
                    remove(from);
                } catch (Exception e) {
                    String msg = e.getMessage();
                    msg = msg.replaceFirst("rm", "mv");
                    throw new Exception(msg);
                }
            }

            if (args[0].equals("ls")){
                done = true;
                if ((args.length != 1)) {
                    throw new Exception(
                            "ls: Invalid number of arguments");
                }
                File file = new File(current);
                String[] fList = file.list();
                for (String s : fList) {
                    System.out.println(s);
                }
            }

            if (args[0].equals("cat")){
                done = true;
                if ((args.length != 2)) {
                    throw new Exception(
                            "cat: Invalid number of arguments");
                }
                File currentFile = new File(current
                        + File.separator + args[1]);
                if (currentFile.exists() && currentFile.isDirectory()) {
                    throw new Exception(
                            "cat: " + args[1] + ": is a directory");
                }
                try {
                        BufferedReader reader = new BufferedReader(
                                new FileReader(currentFile));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            System.out.println(line);
                        }
                        reader.close();
                } catch (IOException e) {
                    throw new Exception(
                            "cat: " + args[1] + ": No such file or directory");
                }
            }

            if (args[0].equals("")) {
                done = true;
            }

            if (args[0].equals("exit")) {
                System.exit(0);
            }

            if (!done){
                throw new Exception(
                        args[0] + ": Unknown command");
            }
        }
        catch (Exception e){
            System.err.println(e.getMessage());
            if (mode) {
                System.exit(-1);
            }
        }

    }
    private static void remove(File file)
            throws Exception {
        if (file.isDirectory()) {
            File[] FList = file.listFiles();
            if (FList == null) {
                throw new Exception(
                         "rm: cannot remove");
            }
            for (File f: FList) {
                remove(f);
            }
        }
        if (!file.delete()) {
            throw new Exception(
                    "rm: cannot remove");
        }
        return;
    }
    private static void copy(File from, File to)
            throws Exception {
        File destination = null;
        try {
            if (to.exists()) {
                if (!to.isDirectory()) {
                    throw new Exception(
                            "cp: " + to.getName() + ": File already exists");
                } else {
                    destination = new File(to.getCanonicalPath()
                            + File.separator + from.getName());
                }
            } else {
                destination = to;
            }
            Files.copy(from.toPath(), destination.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            throw new Exception(
                    "cp: cannot perform this operation");
        }
        if (from.isDirectory()) {
            File[] fList = from.listFiles();
            if (fList == null) {
                throw new Exception(
                        "cp: cannot perform this operation");
            }
            for (File f: fList) {
                copy(f, destination);
            }
        }
    }

}
