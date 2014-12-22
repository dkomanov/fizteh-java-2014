package ru.fizteh.fivt.students.moskupols.cliutils2;

import ru.fizteh.fivt.students.moskupols.cliutils2.commands.Command;

/**
 * Created by moskupols on 02.12.14.
 */
public interface CommandChooser {
    Command commandForArgs(String[] args);
}
