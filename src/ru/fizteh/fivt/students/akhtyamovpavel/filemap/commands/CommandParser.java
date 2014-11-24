package ru.fizteh.fivt.students.akhtyamovpavel.filemap.commands;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by akhtyamovpavel on 29.09.2014.
 */
public class CommandParser {
    private static ArrayList<CommandStorage> parseRequestString(final String request) {
        String[] requestLists = request.split(";");

        ArrayList<CommandStorage> parsedRequestList = new ArrayList<CommandStorage>();
        for (String currentCommand : requestLists) {
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

    public static ArrayList<CommandStorage> parseUserRequest(final String[] arguments) {
        StringBuilder resultString = new StringBuilder();
        for (String currentArgument : arguments) {
            resultString.append(currentArgument);
            resultString.append(" ");
        }
        return parseRequestString(resultString.toString());
    }

    public static ArrayList<CommandStorage> parseUserRequest(final String argument) {
        return parseRequestString(argument);
    }
}
