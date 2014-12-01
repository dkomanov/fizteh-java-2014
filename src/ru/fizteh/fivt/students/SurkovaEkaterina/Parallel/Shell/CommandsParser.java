package ru.fizteh.fivt.students.SurkovaEkaterina.Parallel.Shell;

public class CommandsParser {

    public static String[] parseCommands(final String commands) {
        String[] commandArray = commands.trim().split(";");
        return commandArray;
    }

    public static String getCommandName(final String command) {
        String[] commandArgs = command.trim().split("\\s+");
        return commandArgs[0];
    }

    public static String getCommandParameters(final String command) {

        int spaceIndex = command.trim().indexOf(" ");
        if (spaceIndex == -1) {
            return "";
        } else {
            return command.trim().substring(spaceIndex + 1).trim();
        }
    }

    public static String[] parseCommandParameters(final String parameters) {
        String[] parametersArray = parameters.trim().split("\\s+");
        return parametersArray;
    }
}
