package ru.fizteh.fivt.students.maxim_rep.database.commands;

import ru.fizteh.fivt.students.maxim_rep.shell.commands.ShellCommand;

public interface DBCommand extends ShellCommand {

    boolean execute();

}
