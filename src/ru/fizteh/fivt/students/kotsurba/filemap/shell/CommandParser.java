package ru.fizteh.fivt.students.kotsurba.filemap.shell;

public final class CommandParser {
    private StringBuilder commands;

    public CommandParser(final String[] args) {
        commands = new StringBuilder();
        for (int i = 0; i < args.length; ++i) {
            commands.append(" ").append(args[i]);
        }

        if (commands.charAt(commands.length() - 1) != ';') {
            commands.append(';');
        }
    }

    public CommandParser(final String arg) {
        commands = new StringBuilder(arg);
        if ((!arg.isEmpty()) && (commands.charAt(commands.length() - 1) != ';')) {
            commands.append(';');
        }
    }

    public CommandString getCommand() {
        String command = "";
        for (int i = 0; i < commands.length(); ++i) {
            if (commands.charAt(i) == ';') {
                command = commands.substring(0, i);
                commands.replace(0, i + 1, "");
                break;
            }
        }

        return new CommandString(command);
    }

    public boolean isEmpty() {
        return (commands.length() == 0);
    }
}
