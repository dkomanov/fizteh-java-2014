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
            shellDo(command);
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

    private static void shellExit(String[] args) {
        if (args.length > 1) {
            printError(1, null);
            return;
        }
        System.out.println("Shell exit");
        System.exit(0);
    }


    private static void shellCat(String[] args) {
        if (args.length != 2) {
            printError(2, null);
            return;
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
        }
    }


    private static void shellLs(String[] args) throws NullPointerException {
        if (args.length > 1) {
            printError(1, null);
            return;
        }
        File fl = new File(System.getProperty("user.dir"));
        if (fl.list() != null) {
            for (String tfl : fl.list()) {
                System.out.println(tfl);
            }
        }
    }

    private static void shellCd(String[] path) {
        if (path.length != 2) {
            printError(2, null);
            return;
        }

        if (path[1].equals(".")) {
            return;
        }

        if (path[1].equals("..")) {
            File temp = new File(System.getProperty("user.dir"));
            File fl = new File(temp.getParent());
            System.setProperty("user.dir", fl.getAbsolutePath());
        } else {
            File fl = new File(System.getProperty("user.dir") + "/" + path[1]);
            if (!fl.isDirectory()) {
                printError(8, path[1]);
                return;
            }
            System.setProperty("user.dir", fl.getAbsolutePath());
        }
    }

    private static void shellPwd(String[] args) {
        if (args.length != 1) {
            printError(2, null);
            return;
        }
        System.out.println(System.getProperty("user.dir"));
    }


    private static void shellMkDir(String[] args) {
        if (args.length != 2) {
            printError(2, null);
            return;
        }

        File fl = new File(System.getProperty("user.dir") + "/" + args[1]);
        if (fl.exists() && fl.isDirectory()) {
            printError(4, null);
            return;
        }

        if (!fl.mkdir()) {
            printError(5, args[1]);
        }
    }


    private static void shellRm(String[] args) {
        if ((args.length > 3) | (args.length < 2)) {
            printError(1, null);
            return;
        }
        if (args.length == 3 && !args[1].equals("-r")) {
            printError(2, null);
            return;
        }
        File fl = new File(System.getProperty("user.dir") + "/" + args[args.length - 1]);
        if (!fl.exists()) {
            printError(8, System.getProperty("user.dir") + "/" + args[args.length - 1]);
        }

        if (fl.isDirectory() && (args.length == 2 | (args.length == 3 && !args[1].equals("-r")))) {
            printError(6, args[args.length - 1]);
            return;
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
                }
            }
        }

        if (!fl.delete()) {
            printError(7, args[args.length - 1]);
        }

    }


    private static void shellMv(String[] args) {
        if (3 != args.length) {
            printError(2, null);
            return;
        }


        File fl = new File(System.getProperty("user.dir") + "/" + args[1]);
        if (!fl.exists()) {
            printError(8, args[1]);
            return;
        }

        File nfl = new File(System.getProperty("user.dir") + "/" + args[2]);
        if (nfl.exists() && nfl.isDirectory()) {
            nfl = new File(nfl.getAbsolutePath() + "/" + fl.getName());
        }
        if (!fl.renameTo(nfl)) {
            printError(9, args[1]);
        }
    }


    private static void copy(String now, String to) throws NullPointerException {
        File source = new File(now);
        File path = new File(to);

        if (source.isDirectory()) {
            if (!path.exists()) {
                if (!path.mkdir()) {
                    printError(5, to);
                    return;
                }
            }
            if (source.listFiles() != null) {
                try {
                    for (File file : source.listFiles()) {
                        copy(file.getAbsolutePath(), path.getAbsolutePath() + "/" + file.getName());
                    }
                } catch (NullPointerException e) {
                    System.err.println(e.getMessage());
                }
            }
            return;
        }


        try (FileReader fr = new FileReader(source);
             FileWriter fw = new FileWriter(path)) {
            char[] buf = new char[1];
            while (fr.read(buf) > 0) {
                fw.write(buf);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static void shellCp(String[] args) {
        if (args.length > 4 | args.length < 2) {
            printError(2, null);
            return;
        }
        if (args.length == 4 && !args[1].equals("-r")) {
            printError(2, null);
            return;
        }
        File source = new File(System.getProperty("user.dir") + "/" + args[args.length - 2]);

        if (source.isDirectory() && args.length == 3) {
            printError(6, args[args.length - 2]);
            return;
        }

        File path = new File(System.getProperty("user.dir") + "/" + args[args.length - 1]);

        if (source.isDirectory() && path.isFile()) {
            printError(10, null);
            return;
        }

        if (path.exists() && path.isDirectory()) {
            path = new File(path.getAbsolutePath() + "/" + source.getName());
        }

        if (source.equals(path)) {
            printError(11, null);
            return;
        }

        if (!source.exists()) {
            printError(8, args[args.length - 2]);
            return;
        }
        copy(source.getAbsolutePath(), path.getAbsolutePath());
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

    private static void shellJob(String[] job) {
        if (job == null) {
            return;
        }
        if ((job.length == 0) | (job[0].length() == 0) | job[0] == null) {
            return;
        }

        switch (job[0]) {
            case "cd":
                shellCd(job);
                break;
            case "mkdir":
                shellMkDir(job);
                break;
            case "pwd":
                shellPwd(job);
                break;
            case "rm":
                shellRm(job);
                break;
            case "cp":
                shellCp(job);
                break;
            case "mv":
                shellMv(job);
                break;
            case "ls":
                shellLs(job);
                break;
            case "cat":
                shellCat(job);
                break;
            case "exit":
                shellExit(job);
                break;
            default:
                if (job[0].length() > 0) {
                    printError(3, job[0]);
                    return;
                }
                break;
        }
    }


    private static void shellDo(String command) {
        String[] func = parsCmd(command);
        if (func == null) {
            return;
        }
        for (String name : func) {
            String[] job = parsArg(name);
            shellJob(job);
        }
    }

    private static void shellPack(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
            builder.append(" ");
        }

        String command = new String(builder);

        shellDo(command);
    }
}

