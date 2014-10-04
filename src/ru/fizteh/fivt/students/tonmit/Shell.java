package ru.fizteh.fivt.students.tonmit.shell;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class Shell {
    private static boolean packetMode = false;

    private static void printError(final String errStr) {
        if (packetMode) {
            System.err.println(errStr);
            System.exit(1);
        } else {
            System.out.println(errStr);
        }
    }

    private static boolean check(final int num, final String[] args) {
        if (args.length != num) {
            printError("Incorrect number of args");
            return false;
        }
        return true;
    }

    private String[] getArgsFromString(String str) {
        str = str.trim();
        str = str.replaceAll("[ ]+", " ");
        int countArgs = 1;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) == ' ') {
                ++countArgs;
            }
        }
        if (!str.isEmpty()) {
            Scanner stringScanner = new Scanner(str);
            stringScanner.useDelimiter(" ");
            String[] cmdArgs = new String[countArgs];
            for (int i = 0; stringScanner.hasNext(); ++i) {
                cmdArgs[i] = stringScanner.next();
            }
            stringScanner.close();
            return cmdArgs;
        }
        return null;
    }

    public void start(String[] args) {
        if (args.length != 0) {
            packetMode = true;
            StringBuffer argStr = new StringBuffer(args[0]);
            for (int i = 1; i < args.length; ++i) {
                argStr.append(" ");
                argStr.append(args[i]);
            }
            Scanner mainScanner = new Scanner(argStr.toString());
            mainScanner.useDelimiter("[ ]*;[ ]*");
            while (mainScanner.hasNext()) {
                String str = mainScanner.next();
                execProc(getArgsFromString(str));
            }
            mainScanner.close();
        } else {
            packetMode = false;
            System.out.print("$ ");
            Scanner mainScanner = new Scanner(System.in);
            while (mainScanner.hasNextLine()) {
                String str = new String();
                str = mainScanner.nextLine();
                if (!str.isEmpty()) {
                    Scanner lineSeparator = new Scanner(str);
                    lineSeparator.useDelimiter("[ ]*;[ ]*");
                    while (lineSeparator.hasNext()) {
                        execProc(getArgsFromString(lineSeparator.next()));
                    }
                    lineSeparator.close();
                }
                System.out.print("$ ");
            }
            mainScanner.close();
            return;
        }
    }

    private static String currPath = System.getProperty("user.dir");

    private static File createFile(final String arg) {
        if (new File(arg).isAbsolute()) {
            return new File(arg);
        }
        return new File(currPath + File.separator + arg);
    }

    private void makeDirectory(String arg) {
        File currFile = createFile(arg);
        if (currFile.exists()) {
            printError("mkdir: cannot create a directory '" + arg + "': such directory exists");
        } else if (!currFile.mkdirs()) {
            printError("mkdir cannot create a directory'" + arg + "'");
        }
    }

    private void changeDirectory(final String arg) {
        File currFile = createFile(arg);
        if (currFile.exists()) {
            try {
                currPath = currFile.getCanonicalPath();
            } catch (IOException e) {
                printError("cd: '" + arg + "': No such file or directory");
            }
        } else {
            printError("cd: '" + arg + "': No such file or directory");
        }
    }

    private void ls() {
        File currFile = new File(currPath);
        File[] list = currFile.listFiles();
        if (list != null) {
            for (int i = 0; i < list.length; ++i) {
                if (!list[i].isHidden()) {
                    System.out.println(list[i].getName());
                }
            }
        }
    }

    private boolean copyErrorCheck(final String[] args) {
        if (args.length > 4 || args.length < 3) {
            printError("cp: Wrong number of arguments.");
            return false;
        }
        File currFile = createFile(args[1]);
        File destFile = createFile(args[2]);
        if  (args.length == 4 && !args[1].equals("-r")) {
            printError("cp: Wrong arguments.");
            return false;
        }
        String tmpName = args[1];
        if (args.length == 4) {
            currFile = createFile(args[2]);
            destFile = createFile(args[3]);
            tmpName = args[2];
        }
        if (args.length == 3) {
            currFile = createFile(args[1]);
            destFile = createFile(args[2]);
            if (currFile.isDirectory()) {
                printError("cp: MyFolder is a directory (not copied).");
                return false;
            }
        }
        if (!currFile.exists()) {
            printError("cp: " + tmpName + ": No such file or directory.");
            return false;
        }
        try {
            if (destFile.getCanonicalPath().startsWith(currFile.getCanonicalPath())) {
                printError("cp: copy into self.");
                return false;
            }
            if (Paths.get(destFile.getCanonicalPath(),
                    currFile.getName()).toString().equals(currFile.getCanonicalPath())) {
                printError("cp: paths are equal.");
                return false;
            }
        } catch (IOException e1) {
            printError("cp: copy into self.");
            return false;
        }
        return true;
    }

    private void copy(String[] args) {
        if (!copyErrorCheck(args)) {
            return;
        }
        File currFile = createFile(args[1]);
        File destFile = createFile(args[2]);
        if (args.length == 4) {
            currFile = createFile(args[2]);
            destFile = createFile(args[3]);
        }
        if (args.length == 3 || !currFile.isDirectory()) {
            try {
                destFile = Paths.get(destFile.toString(),
                        currFile.toPath().getFileName().toString()).normalize().toFile();
                Files.copy(currFile.toPath(), destFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES,
                    StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e1) {
                printError("cp: Incorrect arguments.");
                return;
            }
        } else {
            try {
                String[] fileList = currFile.list();
                String directoryName = currFile.getName();
                File destDir = createFile(destFile.getCanonicalPath() + File.separator + directoryName);
                makeDirectory(destDir.getCanonicalPath());
                String[] argStr = new String[]{"cp", "-r", "", destDir.getCanonicalPath()};
                for (int i = 0; i < fileList.length; i++) {
                    argStr[2] = currFile.getCanonicalPath() + File.separator + fileList[i];
                    copy(argStr);
                }
            } catch (IOException e1) {
                    printError("cp: Incorrect arguments.");
                    return;
                }
        }

    }

    private void remove(String[] args) {
        if (args.length > 3 || args.length < 2) {
            printError("rm: Wrong number of parametres.");
            return;
        }
        if (args.length == 3 && !args[1].equals("-r")) {
            printError("rm: Wrong arguments.");
            return;
        }
        File fileToDelete = createFile(args[1]);
        String tmpname = args[1];
        if (args.length == 3) {
            fileToDelete = createFile(args[2]);
            tmpname = args[2];
        }
        if (!fileToDelete.exists()) {
            printError("rm: cannot remove '" + tmpname + "':No such file or directory.");
            return;
        }

        if (!fileToDelete.isDirectory()) {
            if (!fileToDelete.delete()) {
                printError("rm: cannot remove '" + tmpname + "':No such file or directory.");
                return;
            }
        } else {
            try {
                String[] fileList = fileToDelete.list();
                String[] argStr = new String[]{"rm", "-r", ""};
                for (int i = 0; i < fileList.length; i++) {
                    argStr[2] = fileToDelete.getCanonicalPath() + File.separator + fileList[i];
                    remove(argStr);
                }
                if (!fileToDelete.delete()) {
                    printError("rm: cannot remove '" + tmpname + "':No such file or directory.");
                    return;
                }
            } catch (IOException e1) {
                printError("rm: Incorrect arguments.");
                return;
            }
        }
    }

    private void move(final String[] args) {
        if (args.length != 3) {
            printError("mv: Wrong number of arguments.");
            return;
        }
        File currFile = createFile(args[1]);
        File destFile = createFile(args[2]);
        if (!currFile.exists()) {
            printError("mv : cannot move: '" + args[1] + "': No such file or directory");
            return;
        }
        if (destFile.isDirectory()) {
            destFile = createFile(args[2] + File.separator + currFile.getName());
        }
        try {
            if (destFile.getCanonicalPath().startsWith(currFile.getCanonicalPath())) {
                printError("mv: cannot move: '" + args[1] + "': Sourse is parent of destination");
                return;
            }
            if (currFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
                printError("mv: cannot move: '" + args[1] + "': Sourse is the same as destination");
                return;
            }

            Files.move(currFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e1) {
                printError("mv: cannot move '" + args[1] + "'");
                return;
        }
    }

    private void cat(final String[] args) {
        try {
            if (args.length != 2) {
                printError("cat: Wrong number of arguments.");
                return;
            }
            File currFile = createFile(args[1]);
            if (!currFile.exists()) {
                printError("cat: '" + args[1] + "': No such file.");
                return;
            }
            String absPath = currFile.getCanonicalPath();
            FileReader fin = new FileReader(absPath);
            int text;
            while ((text = fin.read()) != -1) {
                System.out.print((char) text);
            }
            System.out.println();
            fin.close();
        } catch (IOException e1) {
            printError("cat: Error occured.");
        }
    }

    private void execProc(final String[] args) {
        if (args != null && args.length != 0) {
            switch (args[0]) {
            case "pwd":
                if (check(1, args)) {
                    System.out.println(currPath);
                }
                break;
            case "cd":
                if (check(2, args)) {
                    changeDirectory(args[1]);
                }
                break;
            case "ls":
                if (check(1, args)) {
                    ls();
                }
                break;
            case "mkdir":
                if (check(2, args)) {
                    makeDirectory(args[1]);
                }
                break;
            case "rm":
                remove(args);
                break;
            case "mv":
                move(args);
                break;
            case "cp":
                    copy(args);
                break;
            case "cat":
                cat(args);
                break;
            case "exit":
                System.exit(0);
                break;
            default:
                printError("Unknown command");
            }
        }
    }

    public static void main(final String[] args) {
        Shell shell = new Shell();
        shell.start(args);
    }
}
