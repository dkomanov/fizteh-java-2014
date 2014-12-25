package ru.fizteh.fivt.students.dsalnikov.shell.commands;

public abstract class AbstractCommand implements Command {

    String commandName;
    int numberOfArgments;

    public AbstractCommand(String commandName, int numberOfArguments) {
        this.commandName = commandName;
        this.numberOfArgments = numberOfArguments;
    }

    @Override
    public String getName() {
        return commandName;
    }

    @Override
    public int getArgsCount() {
        return numberOfArgments;
    }
}
