package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.Table;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class GetCommand extends DbCommand {
    public GetCommand(final TableManager link) {
        super(link);
    }

    @Override
    public boolean checkArgs(int argLen) {
        return (argLen == 2);
    }

    @Override
    public void execute(String[] cmdWithArgs) throws IOException {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalNumberOfArguments(getName());
        }

        Table link = getManager().getUsedTable();
        if (link != null) {
            String value = link.get(cmdWithArgs[1]);
            if (value != null) {
                System.out.println("found");
                System.out.println(value);
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("no table");
        }
    }
}
