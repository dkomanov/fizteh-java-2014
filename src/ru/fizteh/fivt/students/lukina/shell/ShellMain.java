package ru.fizteh.fivt.students.lukina.shell;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public/* abstract */class ShellMain {
    private static String currPath = System.getProperty("user.dir");
    private static boolean isPacket = false;

    private static void printError(String strError) {
        if (isPacket) {
            System.err.println(strError);
            System.exit(1);
        } else {
            System.out.println(strError);
        }
    }

    private static boolean checkArgs(int num, String[] args) {
        return args.length == num;
    }

    private static File createFile(String fileName) {
        if (new File(fileName).isAbsolute()) {
            return new File(fileName);
        }
        return new File(currPath + File.separator + fileName);
    }

    private static void cd(String arg) {
        File currFile = createFile(arg);
        if (currFile.isDirectory()) {
            try {
                currPath = currFile.getCanonicalPath();
            } catch (IOException e) {
                printError("cd: '" + arg + "': No such file or directory");
            }
        } else {
            printError("cd:cd:13: Это не каталог:" + arg);
        }
    }

    public static void mkdir(String arg) {
        File currFile = createFile(arg);
        if (currFile.exists()) {
            printError("mkdir: cannot create a directory '" + arg + "': such directory exists");
        } else if (!currFile.mkdir()) {
            printError("mkdir cannot create a directory'" + arg + "'");
        }
    }

    private static void pwd() {
        System.out.println(currPath);
    }

    private static void doDelete(File currFile) {
        if (currFile.exists()) {
            if (!currFile.isDirectory() || currFile.listFiles().length == 0) {
                currFile.delete();
            } else {
                while (currFile.listFiles().length != 0) {
                    doDelete(currFile.listFiles()[0]);
                }
                currFile.delete();
            }
        }
    }

    public static void rm(String fileName, boolean isRecursive) {
        File currFile = createFile(fileName);
        if (!currFile.exists()) {
            printError("rm: cannot remove '" + fileName
                    + "': No such file or directory");
            return;
        }
        if (isRecursive) {
            doDelete(currFile);
        } else {
            if (!currFile.isDirectory()) {
                currFile.delete();
            } else {
                printError("rm:" + fileName + " : is a directory");
            }
        }
    }

    private static void copy(File from, File to) {
        File destinationFile = null;
        try {
            if (to.exists()) {
                if (!to.isDirectory()) {
                    return;
                } else {
                    destinationFile = createFile(to.getCanonicalPath()
                            + File.separator + from.getName());
                }
            } else {
                destinationFile = to;
            }
            Files.copy(from.toPath(), destinationFile.toPath(),
                    StandardCopyOption.COPY_ATTRIBUTES);
        } catch (IOException e) {
            printError("couldn't copy " + from.getName());
        }
        if (from.isDirectory()) {
            File[] fileNamesList = from.listFiles();
            for (File f : fileNamesList) {
                copy(f, destinationFile);
            }
        }
    }

    private static void cp(boolean recursive, String args1, String args2) {
        File from = createFile(args1);
        File to = createFile(args2);
        try {
            if (from.getCanonicalPath().equals(to.getCanonicalPath())) {
                printError("cp: «" + args1 + "» и «" + args2 + "» - один и тот же файл");
                return;
            }
            if (from.isDirectory() && !to.isDirectory()) {
                printError("cp: невозможно перезаписать поверх файла «" + args2
                        + "», не являющегося каталогом, каталог «" + args1 + "»");
                return;
            }
        } catch (IOException e1) {
            printError("couldn't copy");
        }
        if (recursive && from.isDirectory()) {
            copy(from, to);
        } else if (!from.isDirectory()) {
            File destinationFile = null;
            try {
                if (to.exists()) {
                    if (!to.isDirectory()) {
                        return;
                    } else {
                        destinationFile = createFile(to.getCanonicalPath()
                                + File.separator + from.getName());
                    }
                } else {
                    destinationFile = to;
                }
                Files.copy(from.toPath(), destinationFile.toPath(),
                        StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException e) {
                printError("couldn't copy" + from.getName());
            }
        } else if (from.isDirectory()) {
            printError("cp: пропускается каталог «" + from.getName() + "»");
        }
    }

    // делает все рекурсивно
    public static void mv(String[] args) {
        File currFile = createFile(args[1]);
        File tmpFile = createFile(args[2]);
        File destFile = createFile(args[2] + File.separator + args[1]);
        if (!currFile.exists()) {
            printError("mv: cannot move: '" + args[1]
                    + "': No such file or directory");
        } else if (currFile.isDirectory() && !tmpFile.isDirectory()) {
            printError("mv: невозможно перезаписать поверх файла «" + args[2]
                    + "», не являющегося каталогом, каталог «" + args[1] + "»");
        } else {
            if (!tmpFile.isDirectory()) {
                destFile = tmpFile;
            }
            try {
                Files.move(currFile.toPath(), destFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e1) {
                printError("cp: cannot move '" + args[1] + "'");
            }
        }
    }

    private static void ls() {
        File currFile = createFile(currPath);
        int pathSize = currFile.listFiles().length;
        for (int i = 0; i < pathSize; i++) {
            System.out.println(currFile.listFiles()[i].getName());
        }
    }

    private static void cat(String arg) {
        File currFile = createFile(arg);
        if (!currFile.exists()) {
            printError(arg + " does't exist");
            return;
        }
        if (currFile.isDirectory()) {
            printError("cat:" + arg + ": Это каталог");
            return;
        }
        try {
            String line;
            FileReader file = new FileReader(currFile);
            BufferedReader br = new BufferedReader(file);
            try {
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                printError("couldn't cat" + arg);
            }
        } catch (FileNotFoundException e) {
            printError("file not found" + arg);
        }
    }

    private static String[] getArgsFromString(String str) {
        String newStr = str.replaceAll("[ ]+", " ");
        int countWords = 1;
        for (int i = 0; i < newStr.length(); i++) {
            if (str.charAt(i) == ' ') {
                countWords++;
            }
        }
        String[] args = new String[countWords];
        Scanner scanner = new Scanner(newStr);
        scanner.useDelimiter(" ");
        for (int i = 0; scanner.hasNext(); i++) {
            args[i] = scanner.next();
        }
        scanner.close();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                countWords--;
            }
        }
        String[] arg = new String[countWords];
        for (int i = 0; i < countWords; i++) {
            arg[i] = args[i];
        }
        return arg;
    }

    protected/* abstract */ static void execProc(String[] args) {
        if (args.length != 0) {
            switch (args[0]) {
                case "cd":
                    if (checkArgs(2, args)) {
                        cd(args[1]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "mkdir":
                    if (checkArgs(2, args)) {
                        mkdir(args[1]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "pwd":
                    if (checkArgs(1, args)) {
                        pwd();
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "rm":
                    if (checkArgs(2, args)) {
                        rm(args[1], false);
                    } else if (checkArgs(3, args) && args[1].equals("-r")) {
                        rm(args[2], true);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "cp":
                    if (checkArgs(3, args)) {
                        cp(false, args[1], args[2]);
                    } else if (checkArgs(4, args) && args[1].equals("-r")) {
                        cp(true, args[2], args[3]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "mv":
                    if (checkArgs(3, args)) {
                        mv(args);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "ls":
                    if (checkArgs(1, args)) {
                        ls();
                    } else {
                        printError("unknown command format");
                    }
                    break;
                case "cat":
                    if (checkArgs(2, args)) {
                        cat(args[1]);
                    } else {
                        printError("unknown command format");
                    }
                    break;
                default:
                    printError("unknown_command");
            }
        }
    }

    /*
     * сначала аргументы делятся по пробелу.а мы их склеим и поделим по ";"потом
     * вызовем для каждой группы аргументовфункцию разбивающую аргументы снова
     * как нужнои выполняющую программки
     */
    public static void exec(String[] args) {
        if (args.length != 0) {
            isPacket = true;
            StringBuffer str = new StringBuffer(args[0]);
            for (int i = 1; i < args.length; i++) {
                str.append(" ");
                str.append(args[i]);
            }
            Scanner scanner = new Scanner(str.toString());
            scanner.useDelimiter("[ ]*;[ ]*");
            while (scanner.hasNext()) {
                String string = "";
                string = scanner.next();
                if (!string.isEmpty()) {
                    execProc(getArgsFromString(string));
                }
            }
            scanner.close();
        } else {
            isPacket = false;
            System.out.print("$ ");
            Scanner scanner = new Scanner(System.in);
            scanner.useDelimiter(System.lineSeparator());
            while (scanner.hasNextLine()) {
                Scanner scannerParse = new Scanner(scanner.next());
                scannerParse.useDelimiter("[ ]*;[ ]*");
                while (scannerParse.hasNext()) {
                    String string = "";
                    string = scannerParse.next();
                    if (string.equals("exit")) {
                        scanner.close();
                        scannerParse.close();
                        return;
                    }
                    if (!string.isEmpty()) {
                        execProc(getArgsFromString(string));
                    }
                }
                System.out.print("$ ");
            }
            scanner.close();
        }
    }

    public static void main(String[] args) {
        ShellMain.exec(args);
        System.exit(0);
    }
}
