package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class NameFirstCommand extends Command {

    @Override
    public String checkArgs(String[] args) {
        return name().equals(args[0]) ? null : name() + "should be the first argument";
    }
}
