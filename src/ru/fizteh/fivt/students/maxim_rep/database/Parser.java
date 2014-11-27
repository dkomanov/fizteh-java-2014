package ru.fizteh.fivt.students.maxim_rep.database;

import ru.fizteh.fivt.students.maxim_rep.database.commands.*;

import java.util.ArrayList;

public class Parser {

    public static String makeStringCommand(String[] args) {
        String commandLine = "";
        for (int i = 0; i < args.length; i++) {
            commandLine = commandLine + args[i] + " ";
        }

        return commandLine;
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

    public static boolean argsMatch(String command, int num, int needed) {
        if (num > needed) {
            System.out.println(command + ": too much arguments");
            return false;
        }
        return true;
    }

    public static String[] divideByChar(String args, String parseBy) {
        args = args.trim();
        return args.split(parseBy);
    }

    public static DBCommand getCommandFromString(String str) {
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
            case "put":
                if (!argsMatch(comName, argsNum, 3)) {
                    return new EmptyCommand();
                }
                return new Put(DbMain.filePath, comArgs[1], comArgs[2]);
            case "get":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Get(DbMain.filePath, comArgs[1]);
            case "":
                return new EmptyCommand();
            case "remove":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Remove(DbMain.filePath, comArgs[1]);
            case "list":
                if (!argsMatch(comName, argsNum, 1)) {
                    return new EmptyCommand();
                }
                return new List(DbMain.filePath);
            default:
                return new UnknownCommand(str);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(str + ": Wrong command syntax");
            return new EmptyCommand();

        }
    }
}
