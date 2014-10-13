package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.io.IOException;

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
            System.out.println(String.join(", ", link.list()));
        } else {
            System.out.println("no table");
        }
    }
}
