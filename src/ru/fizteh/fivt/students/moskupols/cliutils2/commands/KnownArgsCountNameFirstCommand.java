package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class KnownArgsCountNameFirstCommand extends NameFirstCommand {
    int expectedArgsCount() {
        return 1;
    }

    @Override
    public String checkArgs(String[] args) {
        String ret = super.checkArgs(args);
        if (ret == null && args.length != expectedArgsCount()) {
            ret = String.format("%d arguments expected", expectedArgsCount());
        }
        return ret;
    }
}
