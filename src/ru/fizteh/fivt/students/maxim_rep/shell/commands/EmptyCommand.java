package ru.fizteh.fivt.students.maxim_rep.shell.commands;

public class EmptyCommand implements ShellCommand {

    public EmptyCommand() {
    }

    @Override
    public boolean execute() {
        return true;
    }

}
