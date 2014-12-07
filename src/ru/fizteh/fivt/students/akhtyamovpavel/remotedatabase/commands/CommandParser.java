package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.commands;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class CommandParser {
    private static ArrayList<CommandStorage> parseRequestString(final String request) throws ParseException {
        String[] requestLists = request.split(";");
        ArrayList<String> parsedLists = new ArrayList<>();
        for (String string : requestLists) {

            boolean isBrace = false;
            int lastIndex = 0;
            StringBuilder resultQuery = new StringBuilder();
            for (int j = 0; j < string.length(); ++j) {
                if (isBrace && string.charAt(j) == ']') {
                    isBrace = false;
                    resultQuery.append(parseJSON(string.substring(lastIndex, j + 1)));
                    lastIndex = j + 1;
                    resultQuery.append(" ");
                } else if (!isBrace && string.charAt(j) == '[') {
                    isBrace = true;
                    resultQuery.append(parseJSON(string.substring(lastIndex, j)));
                    lastIndex = j;

                    resultQuery.append(" ");
                }
            }
            if (lastIndex != string.length()) {
                resultQuery.append(parseJSON(string.substring(lastIndex, string.length())));
            }
            if (isBrace) {
                throw new ParseException("parse: wrong command", 0);
            }
            parsedLists.add(resultQuery.toString());
        }


        ArrayList<CommandStorage> parsedRequestList = new ArrayList<>();
        for (String currentCommand : parsedLists) {

            String[] requestTokens = currentCommand.split("\\s+");


            ArrayList<String> commandTokensList = new ArrayList<String>(Arrays.asList(requestTokens));

            if (!commandTokensList.isEmpty() && commandTokensList.get(0).equals("")) {
                commandTokensList.remove(0);
            }

            if (!commandTokensList.isEmpty()) {
                parsedRequestList.add(new CommandStorage(commandTokensList));
            }

        }
        return parsedRequestList;
    }

    public static ArrayList<CommandStorage> parseUserRequest(final String[] arguments) throws ParseException {
        StringBuilder resultString = new StringBuilder();
        for (String currentArgument : arguments) {
            resultString.append(currentArgument);
            resultString.append(" ");
        }
        return parseRequestString(resultString.toString());
    }

    public static ArrayList<CommandStorage> parseUserRequest(final String argument) throws ParseException {
        return parseRequestString(argument);
    }

    public static String parseJSON(String string) {
        string = string.trim();
        if (string.isEmpty() || string.charAt(0) != '[' || string.charAt(string.length() - 1) != ']') {
            return string;
        }
        StringBuilder sb = new StringBuilder();
        String argumentsString = string.substring(1, string.length() - 1).trim();
        String[] stringsJSON = argumentsString.split(",");
        for (int i = 0; i < stringsJSON.length; ++i) {
            stringsJSON[i] = stringsJSON[i].trim();
        }

        sb.append('[');
        for (int i = 0; i < stringsJSON.length; ++i) {
            sb.append(stringsJSON[i]);
            if (i + 1 != stringsJSON.length) {
                sb.append(',');
            }
        }
        sb.append(']');

        return sb.toString();
    }
}
