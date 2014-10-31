package ru.fizteh.fivt.students.maxim_rep.multifilehashmap;

import ru.fizteh.fivt.students.maxim_rep.multifilehashmap.commands.*;

import java.util.ArrayList;

public class Parser {

    public static String makeStringCommand(String[] args) {
        StringBuilder commandLine = new StringBuilder("");
        for (int i = 0; i < args.length; i++) {
            commandLine.append(args[i] + " ");
        }

        return commandLine.toString();
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
        } else if (num < needed) {
            System.out.println(command + ": Wrong command syntax");
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
                return new Put(comArgs[1], comArgs[2]);
            case "get":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Get(comArgs[1]);
            case "use":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Use(comArgs[1]);
            case "show":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                if (!comArgs[1].equals("tables")) {
                    return new UnknownCommand(str);
                }
                return new Show();
            case "drop":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Drop(comArgs[1]);
            case "create":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Create(comArgs[1]);
            case "":
                return new EmptyCommand();
            case "remove":
                if (!argsMatch(comName, argsNum, 2)) {
                    return new EmptyCommand();
                }
                return new Remove(comArgs[1]);
            case "list":
                if (!argsMatch(comName, argsNum, 1)) {
                    return new EmptyCommand();
                }
                return new List();
            default:
                return new UnknownCommand(str);
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            if (comName.equals("show")) {
                return new UnknownCommand("show");
            }

            return new EmptyCommand();

        }
    }
}
