package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.Table;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class PutCommand extends DbCommand {
    public PutCommand(final TableManager link) {
       super(link);
    }

    @Override
    public boolean checkArgs(final int argLen) {
        return (argLen == 3);
    }

    @Override
    public void execute(final String[] cmdWithArgs) {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalArgumentException(getName()
                    + ": Incorrect number of arguments");
        }

        Table link = getManager().getUsedTable();
        if (link != null) {
            String oldValue = link.put(cmdWithArgs[1], cmdWithArgs[2]);
            if (oldValue != null) {
                System.out.println("overwrite");
                System.out.println(oldValue);
            } else {
                System.out.println("new");
            }
        } else {
            System.out.println("no table");
        }
    }
}
