package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.util.Map;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.DbConnector;

public final class PutCommand extends DbCommand {
    public PutCommand(final DbConnector link) {
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

        Map<String, String> dataBase = getConnector().getDataBase();
        String oldValue = dataBase.put(cmdWithArgs[1], cmdWithArgs[2]);
        if (oldValue != null) {
            System.out.println("overwrite");
            System.out.println(oldValue);
        } else {
            System.out.println("new");
        }
    }
}
