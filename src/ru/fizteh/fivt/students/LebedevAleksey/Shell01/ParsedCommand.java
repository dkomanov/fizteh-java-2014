package ru.fizteh.fivt.students.LebedevAleksey.Shell01;

public class ParsedCommand {
    private String[] arguments;
    private String commandName;

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }
}