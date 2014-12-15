package ru.fizteh.fivt.students.kinanAlsarmini.shell.commands;

import java.util.ArrayList;
import java.util.regex.*;

public class CommandParser {
    public static ArrayList<String> parseParams(String params) {
        ArrayList<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher regexMatcher = regex.matcher(params);

        while (regexMatcher.find()) {
            String param = regexMatcher.group().replaceAll("\"?([~\"]*)\"?", "$1");

            matchList.add(param);
        }

        return matchList;
    }

    public static String getCommandName(String command) {
        int splitIndex = command.indexOf(' ');

        if (splitIndex == -1) {
            return command;
        } else {
            return command.substring(0, splitIndex);
        }
    }

    public static String getParameters(String command) {
        int splitIndex = command.indexOf(' ');

        if (splitIndex == -1) {
            return "";
        } else {
            return command.substring(splitIndex + 1);
        }
    }

    public static int getParametersCount(String parameters) {
        ArrayList<String> params = parseParams(parameters);

        return params.size();
    }

    public static String[] parseCommands(String commands) {
        String[] commandArray = commands.split(";");

        for (int i = 0; i < commandArray.length; ++i) {
            commandArray[i] = commandArray[i].trim();
        }

        return commandArray;
    }
}
