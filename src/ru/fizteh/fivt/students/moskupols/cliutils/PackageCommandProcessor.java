package ru.fizteh.fivt.students.moskupols.cliutils;

/**
 * Created by moskupols on 28.09.14.
 */
public class PackageCommandProcessor implements CommandProcessor {
    private final String[] splittedCommands;

    public PackageCommandProcessor(String[] args) {
        StringBuilder joiner = new StringBuilder();
        for (String s : args) {
            joiner.append(s);
            joiner.append(' ');
        }
        splittedCommands = joiner.toString().split(";");
    }

    @Override
    public void process(CommandFactory commandFactory)
            throws CommandExecutionException, UnknownCommandException {
        for (String s : splittedCommands) {
            boolean needed = true;
            try {
                commandFactory.fromString(s).execute();
            } catch (StopProcessingException e) {
                needed = false;
            }
            if (!needed) {
                break;
            }
        }
    }
}
