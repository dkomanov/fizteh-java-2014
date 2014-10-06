package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.util.Map;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.DbConnector;

public final class RemoveCommand extends DbCommand {
    public RemoveCommand(final DbConnector link) {
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

        Map<String, String> dataBase = getConnector().getDataBase();
        String removedValue = dataBase.remove(cmdWithArgs[1]);
        if (removedValue != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }

}
