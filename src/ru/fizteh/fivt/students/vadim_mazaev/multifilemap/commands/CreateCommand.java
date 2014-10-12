package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class CreateCommand extends DbCommand {
    public CreateCommand(TableManager manager) {
        super(manager);
    }

    @Override
    public boolean checkArgs(int argLen) {
        return (argLen == 2);
    }

    @Override
    public void execute(String[] cmdWithArgs) {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalNumberOfArguments(getName());
        }
        
        if (getManager().createTable(cmdWithArgs[1]) != null) {
            System.out.println("created");
        } else {
            System.out.println(cmdWithArgs[1] + " exists");
        }
    }
}
