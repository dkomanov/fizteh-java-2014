package ru.fizteh.fivt.students.SukhanovZhenya.shell;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0) {
            shellPack(args);
            System.exit(0);
        }

        while (true) {
            shellStart();
            String command = shellRead();
            shellDo(command, false);
        }
    }

    private static void printError(int num, String cause) {
        switch (num) {
            case 1:
                System.err.println("Unknown arguments!");
                break;
            case 2:
                System.err.println("Incorrect arguments!");
                break;
            case 3:
                System.err.println("Unknown command: " + cause + "!");
                break;
            case 4:
                System.err.println("Can't create! There is a directory with the same name!");
                break;
            case 5:
                System.err.println("Can't create directory: " + cause + "!");
                break;
            case 6:
                System.err.println("This is a directory: " + cause + "!");
                break;
            case 7:
                System.err.println("Can't remove: " + cause + "!");
                break;
            case 8:
                System.err.println("There is no such file or directory: " + cause + "!");
                break;
            case 9:
                System.err.println("Can't move this file: " + cause + "!");
                break;
            case 10:
                System.err.println("Can't copy directory to the file!");
                break;
            case 11:
                System.err.println("This files are equal!");
                break;
            default:
                break;
        }
    }

    private static String[] parsCmd(String s) {
        return s.split(" *;+( *;*)*");
    }

    private static String[] parsArg(String s) {
        return s.split(" +");
    }

    private static boolean shellExit(String[] args) {
        if (args.length > 1) {
            printError(1, null);
            return false;
        }
        System.exit(0);
        return true;
    }


    private static boolean shellCat(String[] args) {
        if (args.length != 2) {
            printError(2, null);
            return false;
        }

        try (FileReader fr = new FileReader(System.getProperty("user.dir") + "/" + args[1])) {
            char[] a = new char[1];

            while (fr.read(a) != -1) {
                for (char c : a) {
                    System.out.print(c);
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }


    private static boolean shellLs(String[] args) throws NullPointerException {
        if (args.length > 1) {
            printError(1, null);
            return false;
        }
        File fl = new File(System.getProperty("user.dir"));
        if (fl.list() != null) {
            for (String tfl : fl.list()) {
                System.out.println(tfl);
            }
        }
        return true;
    }

    private static boolean shellCd(String[] path) {
        if (path.length != 2) {
            printError(2, null);
            return false;
        }

        if (path[1].equals(".")) {
            return true;
        }

        if (path[1].equals("..")) {
            File temp = new File(System.getProperty("user.dir"));
            File fl = new File(temp.getParent());
            System.setProperty("user.dir", fl.getAbsolutePath());
        } else {
            File fl = new File(System.getProperty("user.dir") + "/" + path[1]);
            if (!fl.isDirectory()) {
                printError(8, path[1]);
                return false;
            }
            System.setProperty("user.dir", fl.getAbsolutePath());
        }
        return true;
    }

    private static boolean shellPwd(String[] args) {
        if (args.length != 1) {
            printError(2, null);
            return false;
        }
        System.out.println(System.getProperty("user.dir"));
        return true;
    }


    private static boolean shellMkDir(String[] args) {
        if (args.length != 2) {
            printError(2, null);
            return false;
        }

        File fl = new File(System.getProperty("user.dir") + "/" + args[1]);
        if (fl.exists() && fl.isDirectory()) {
            printError(4, null);
            return false;
        }

        if (!fl.mkdir()) {
            printError(5, args[1]);
            return false;
        }
        return true;
    }


    private static boolean shellRm(String[] args) {
        if ((args.length > 3) | (args.length < 2)) {
            printError(1, null);
            return false;
        }
        if (args.length == 3 && !args[1].equals("-r")) {
            printError(2, null);
            return false;
        }
        File fl = new File(System.getProperty("user.dir") + "/" + args[args.length - 1]);
        if (!fl.exists()) {
            printError(8, System.getProperty("user.dir") + "/" + args[args.length - 1]);
            return false;
        }

        if (fl.isDirectory() && (args.length == 2 | (args.length == 3 && !args[1].equals("-r")))) {
            printError(6, args[args.length - 1]);
            return false;
        }
        if (fl.isDirectory()) {
            if (fl.listFiles() != null) {
                try {
                    for (File file : fl.listFiles()) {
                        String[] tmp = {"rm", "-r", args[args.length - 1] + "/" + file.getName()};
                        shellRm(tmp);
                    }
                } catch (NullPointerException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }
        }

        if (!fl.delete()) {
            printError(7, args[args.length - 1]);
            return false;
        }
        return true;
    }


    private static boolean shellMv(String[] args) {
        if (3 != args.length) {
            printError(2, null);
            return false;
        }


        File fl = new File(System.getProperty("user.dir") + "/" + args[1]);
        if (!fl.exists()) {
            printError(8, args[1]);
            return false;
        }

        File nfl = new File(System.getProperty("user.dir") + "/" + args[2]);
        if (nfl.exists() && nfl.isDirectory()) {
            nfl = new File(nfl.getAbsolutePath() + "/" + fl.getName());
        }
        if (!fl.renameTo(nfl)) {
            printError(9, args[1]);
            return false;
        }
        return true;
    }


    private static boolean copy(String now, String to) throws NullPointerException {
        File source = new File(now);
        File path = new File(to);

        if (source.isDirectory()) {
            if (!path.exists()) {
                if (!path.mkdir()) {
                    printError(5, to);
                    return false;
                }
            }
            if (source.listFiles() != null) {
                try {
                    for (File file : source.listFiles()) {
                        copy(file.getAbsolutePath(), path.getAbsolutePath() + "/" + file.getName());
                    }
                } catch (NullPointerException e) {
                    System.err.println(e.getMessage());
                    return false;
                }
            }
            return true;
        }


        try (FileReader fr = new FileReader(source);
             FileWriter fw = new FileWriter(path)) {
            char[] buf = new char[1];
            while (fr.read(buf) > 0) {
                fw.write(buf);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    private static boolean shellCp(String[] args) {
        if (args.length > 4 | args.length < 2) {
            printError(2, null);
            return false;
        }
        if (args.length == 4 && !args[1].equals("-r")) {
            printError(2, null);
            return false;
        }
        File source = new File(System.getProperty("user.dir") + "/" + args[args.length - 2]);
        File path = new File(System.getProperty("user.dir") + "/" + args[args.length - 1]);

        if (source.equals(path)) {
            printError(11, null);
            return false;
        }

        if (source.isDirectory() && args.length == 3) {
            printError(6, args[args.length - 2]);
            return false;
        }

        if (source.isDirectory() && path.isFile()) {
            printError(10, null);
            return false;
        }

        if (path.exists() && path.isDirectory()) {
            path = new File(path.getAbsolutePath() + "/" + source.getName());
        }

        if (!source.exists()) {
            printError(8, args[args.length - 2]);
            return false;
        }
        return copy(source.getAbsolutePath(), path.getAbsolutePath());
    }

    private static void shellStart() {
        System.out.print(System.getProperty("user.name") + " $:");
    }

    private static String shellRead() {
        try {
            Scanner scanner = new Scanner(System.in);
            return scanner.nextLine();
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }
        return null;
    }

    private static void shellJob(String[] job, boolean ifPack) {
        if (job == null) {
            return;
        }
        if ((job.length == 0) | (job[0].length() == 0) | job[0] == null) {
            return;
        }

        boolean res = true;

        switch (job[0]) {
            case "cd":
                res = shellCd(job);
                break;
            case "mkdir":
                res = shellMkDir(job);
                break;
            case "pwd":
                res = shellPwd(job);
                break;
            case "rm":
                res = shellRm(job);
                break;
            case "cp":
                res = shellCp(job);
                break;
            case "mv":
                res = shellMv(job);
                break;
            case "ls":
                res = shellLs(job);
                break;
            case "cat":
                res = shellCat(job);
                break;
            case "exit":
                res = shellExit(job);
                break;
            default:
                if (job[0].length() > 0) {
                    printError(3, job[0]);
                    res = false;
                }
                break;
        }
        if ((!res) && ifPack) {
            System.exit(1);
        }
    }


    private static void shellDo(String command, boolean ifPack) {
        String[] func = parsCmd(command);
        if (func == null) {
            return;
        }
        for (String name : func) {
            String[] job = parsArg(name);
            shellJob(job, ifPack);
        }
    }

    private static void shellPack(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
            builder.append(" ");
        }

        String command = new String(builder);

        shellDo(command, true);
    }
}

