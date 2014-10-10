package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.util.List;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.Table;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class ListCommand extends DbCommand {
    public ListCommand(final TableManager link) {
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

        Table link = getManager().getUsedTable();
        if (link != null) {
            List<String> keys = link.list();
            for (String current : keys) {
                System.out.print(current);
                System.out.print(" ");
            }
            System.out.println();
        } else {
            System.out.println("no table");
        }
    }

}
