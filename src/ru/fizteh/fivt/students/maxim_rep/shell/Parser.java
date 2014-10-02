package ru.fizteh.fivt.students.maxim_rep.shell;

import ru.fizteh.fivt.students.maxim_rep.shell.commands.*;

import java.io.File;
import java.util.ArrayList;

public class Parser {

    public static String makeStringCommand(String[] args) {
        String commandLine = "";
        for (int i = 0; i < args.length; i++) {
            commandLine = commandLine + args[i] + " ";
        }

        return commandLine;
    }

    public static String pathConverter(String path, String currentPath) {
        File f = new File(currentPath);

        if (path.equals("/") || path.equals("\\")) {
            return "/";
        } else if (path.startsWith("~")) {
            return System.getProperty("user.home") + path.substring(1);
        } else if (path.equals("")) {
            return System.getProperty("user.home");
        } else if (path.length() >= 2 && path.substring(0, 2).equals("..")) {
            if (f.getParent() == null) {
                return "/" + path.substring(2);
            }
            return f.getParent() + path.substring(2);
        } else if (path.startsWith(".")
                && (path.startsWith("/", 1) || path.startsWith("\\", 1))) {
            return currentPath + path.substring(1);
        }

        if (!(path.startsWith("/", 0) || path.startsWith("\\", 0) || path
                .startsWith(":", 1))) {
            if (currentPath.equals("/")) {
                path = currentPath + path;
            } else {
                path = currentPath + "/" + path;
            }
        }

        return path;
    }

    public static String[] commandToArguments(String command) {
        StringBuilder newLine = new StringBuilder("");
        ArrayList<String> tbl = new ArrayList<String>();

        boolean quoted = false;
        for (char curChar : command.toCharArray()) {
            if (curChar == '"') {

                if (newLine.length() > 0
                        && newLine.charAt(newLine.length() - 1) == '\\') {
                    newLine.deleteCharAt(newLine.length() - 1);
                    newLine.insert(newLine.length(), curChar);
                    continue;
                } else {
                    quoted = !quoted;
                    continue;
                }

            }
            if (Character.isWhitespace(curChar)) {
                if (quoted) {
                    newLine.append(curChar);
                    continue;
                } else {
                    if (!newLine.toString().equals("")) {
                        tbl.add(newLine.toString());
                    }
                    newLine = new StringBuilder("");
                    continue;
                }

            }
            newLine.append(curChar);
        }

        if (!newLine.toString().equals(" ")) {
            tbl.add(newLine.toString());
        }
        String[] resultArr = new String[tbl.size()];
        resultArr = tbl.toArray(resultArr);

        return resultArr;

    }

    public static String[] divideByChar(String args, String parseBy) {
        args = args.trim();
        return args.split(parseBy);
    }

    public static boolean argsMatch(String command, int num, int needed) {
        if (num > needed) {
            System.out.println(command + ": too much arguments");
            return false;
        }
        return true;
    }

    public static ShellCommand getCommandFromString(String str) {
        String[] comArgs = Parser.commandToArguments(str);
        String comName = comArgs[0];
        int argsNum = comArgs.length;

        try {
            switch (comName) {
            case "exit":
                if (!argsMatch(comName, argsNum, 1)) {
                    return new EmptyCommand();
                }
                return new Exit();
            case "cd":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Cd(Shell.currentPath, comArgs[1]);
            case "cat":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Cat(Shell.currentPath, comArgs[1]);
            case "mv":
                if (!argsMatch(comName, argsNum, 3)) {
                    return new EmptyCommand();
                }
                return new Mv(Shell.currentPath, comArgs[1], comArgs[2]);
            case "cp":
                if (comArgs[1].equals("-r")) {
                    if (!argsMatch(comName, argsNum, 4)) {
                        return new EmptyCommand();
                    }
                    return new Cp(Shell.currentPath, comArgs[2], comArgs[3],
                            true);
                } else {
                    if (!argsMatch(comName, argsNum, 3)) {
                        return new EmptyCommand();
                    }
                    return new Cp(Shell.currentPath, comArgs[1], comArgs[2],
                            false);
                }
            case "rm":
                if (comArgs[1].equals("-r")) {
                    if (!argsMatch(comName, argsNum, 3)) {
                        return new EmptyCommand();
                    }
                    return new Rm(Shell.currentPath, comArgs[2], true);
                } else {
                    if (!argsMatch(comName, argsNum, 2)) {
                        return new EmptyCommand();
                    }
                    return new Rm(Shell.currentPath, comArgs[1], false);
                }
            case "mkdir":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Mkdir(Shell.currentPath, comArgs[1]);

            case "":
                return new EmptyCommand();

            case "ls":
                if (!argsMatch(comName, argsNum, 1)) {
                    return new EmptyCommand();
                }
                return new Ls(Shell.currentPath);
            case "pwd":
                if (!argsMatch(comName, argsNum, 1)) {
                    return new EmptyCommand();
                }
                return new Pwd(Shell.currentPath);
            default:
                return new UnknownCommand(str);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            if (comName.equals("cd")) {
                return new Cd(Shell.currentPath, "");
            }

            System.out.println(str + ": Wrong command syntax");
            return new EmptyCommand();

        }
    }
}
