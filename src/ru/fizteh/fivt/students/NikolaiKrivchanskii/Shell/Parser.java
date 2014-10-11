package ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    
    
    public static ArrayList<String> parseCommandArgs(String params) {
        ArrayList<String> matchList = new ArrayList<String>();
        Pattern regex = Pattern.compile("[^\\s\"']+|\"[^\"]*\"|'[^']*'");
        Matcher regexMatcher = regex.matcher(params);
        while (regexMatcher.find()) {
            String param = regexMatcher.group().replaceAll("\"?([~\"]*)\"?", "$1");
            matchList.add(param);
        }
        return matchList;
    }
    
    public static String getName(String command) {
        int spiltIndex = command.indexOf(' ');
        if (spiltIndex == -1) {
            return command;
        } else {
            return command.substring(0, spiltIndex);
        }
    }

    public static String[] parseFullCommand(String commands) {
        String[] commandArray = commands.split(";");
        for (int i = 0; i < commandArray.length; ++i) {
            commandArray[i] = commandArray[i].trim();
        }
        return commandArray;
    }
    public static String getParameters(String command) {
        int spiltIndex = command.indexOf(' ');
        if (spiltIndex == -1) {
            return "";
        } else {
            return command.substring(spiltIndex + 1);
        }
    }
}
