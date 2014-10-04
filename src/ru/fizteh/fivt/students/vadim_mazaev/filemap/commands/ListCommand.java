package ru.fizteh.fivt.students.vadim_mazaev.filemap.commands;

import java.util.Set;

import ru.fizteh.fivt.students.vadim_mazaev.filemap.DbConnector;

public final class ListCommand extends DbCommand {
    public ListCommand(final DbConnector link) {
        super(link);
    }

    @Override
    public boolean checkArgs(final int argLen) {
        return (argLen == 1);
    }

    @Override
    public void execute(final String[] cmdWithArgs) {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalArgumentException(getName()
                    + ": Incorrect number of arguments");
        }

        Set<String> keys = getConnector().getDataBase().keySet();
        for (String current : keys) {
            System.out.print(current);
            System.out.print(" ");
        }
        System.out.println();
    }

}
