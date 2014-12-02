package ru.fizteh.fivt.students.moskupols.cliutils2.commands;

import java.util.Arrays;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class FixedArgsNameFirstCommand extends NameFirstCommand {
    abstract String[] expectedArgs();

    @Override
    public String checkArgs(String[] args) {
        String ret = super.checkArgs(args);
        if (ret == null && !Arrays.equals(args, expectedArgs())) {
            ret = "This command should be called only in '" + String.join(" ", expectedArgs()) + "' form";
        }
        return ret;
    }
}
