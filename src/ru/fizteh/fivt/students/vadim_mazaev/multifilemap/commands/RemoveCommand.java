package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.Table;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class RemoveCommand extends DbCommand {
    public RemoveCommand(TableManager link) {
        super(link);
    }

    @Override
    public boolean checkArgs(int argLen) {
        return (argLen == 2);
    }

    @Override
    public void run(String[] cmdWithArgs) throws IOException {
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
