package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.io.IOException;
import java.util.List;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.Table;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class ListCommand extends DbCommand {
    public ListCommand(TableManager link) {
        super(link);
    }

    @Override
    public boolean checkArgs(int argLen) {
        return (argLen == 1);
    }

    @Override
    public void execute(String[] cmdWithArgs) throws IOException {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalNumberOfArguments(getName());
        }

        Table link = getManager().getUsedTable();
        if (link != null) {
            List<String> keys = link.list();
            for (String current : keys) {
                System.out.print(current);
                //TODO add ","
                System.out.print(" ");
            }
            System.out.println();
        } else {
            System.out.println("no table");
        }
    }
}
