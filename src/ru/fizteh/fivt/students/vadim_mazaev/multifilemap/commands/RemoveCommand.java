package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.Table;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class RemoveCommand extends DbCommand {
    public RemoveCommand(final TableManager link) {
        super(link);
    }

    @Override
    public boolean checkArgs(final int argLen) {
        return (argLen == 2);
    }

    @Override
    public void execute(final String[] cmdWithArgs) {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalArgumentException(getName()
                    + ": Incorrect number of arguments");
        }

        Table link = getManager().getUsedTable();
        if (link != null) {
            String removedValue = link.remove(cmdWithArgs[1]);
            if (removedValue != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }
    }

}
