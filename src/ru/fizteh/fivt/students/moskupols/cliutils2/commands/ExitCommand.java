package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

/**
 * Created by moskupols on 03.12.14.
 */
public class ExitCommand extends KnownArgsCountNameFirstCommand implements FinalizerCommand {
    @Override
    public int expectedArgsCount() {
        return 1;
    }

    @Override
    public String name() {
        return "exit";
    }

    @Override
    protected void performAction(Object context, String[] args) {
        // Nothing to do by default
    }
}
