package ru.fizteh.fivt.students.sautin1.shell;

/**
 * Created by sautin1 on 9/30/14.
 */
public class ShellMain {
    private static final String commandDelimiter = "\\s*;\\s*";
    private static final String paramDelimiter = "\\s+";

    private static String convertArrayToString(String... stringArray) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(string);
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    private static String[] splitStringIntoCommands(String string) {
        string = string.trim();
        return string.split(commandDelimiter);
    }

    private static String[] splitCommandIntoParams(String string) {
        return string.split(paramDelimiter);
    }

    public static void main(String[] args) {
        Shell shell = new Shell();
        if (args.length == 0) {
            // interactive mode
            shell.interactWithUser();
        } else {
            // non-interactive mode
            String[] commandArray = splitStringIntoCommands(convertArrayToString(args));
            for (String command : commandArray) {
                String[] params = splitCommandIntoParams(command);
                try {
                    shell.executeCommand(params);
                } catch (IllegalArgumentException e) {
                    System.err.println(e.getMessage());
                    System.out.println(e.getMessage());
                    System.exit(1);
                }
            }
        }
    }

}
