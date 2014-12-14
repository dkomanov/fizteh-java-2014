package ru.fizteh.fivt.students.maxim_rep.database.commands;

public class EmptyCommand implements DBCommand {
    @Override
    public boolean execute() {
        return false;
    }
}
