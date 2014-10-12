package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class UseCommand extends DbCommand {
    public UseCommand(TableManager link) {
        super(link);
    }

    @Override
    public boolean checkArgs(int argLen) {
        return (argLen == 2);
    }

    public void execute(String[] cmdWithArgs) throws IOException {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalNumberOfArguments(getName());
        }

        if (getManager().useTable(cmdWithArgs[1])) {
            System.out.println("using " + cmdWithArgs[1]);
        } else {
            System.out.println(cmdWithArgs[1] + " not exists");
        }
    }
}
