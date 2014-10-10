package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.util.Set;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class ShowTablesCommand extends DbCommand {
    public ShowTablesCommand(TableManager link) {
        super(link);
    }

    @Override
    public boolean checkArgs(int argLen) {
        return (argLen == 2);
    }

    @Override
    public void execute(String[] cmdWithArgs) {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalArgumentException(getName()
                    + ": Incorrect number of arguments");
        }

        System.out.println("Tablename raw_count");
        Set<String> tables = getManager().getTablesSet();
        for (String curTable : tables) {
            System.out.println(curTable + " " + getManager().getTable(curTable).size());
        }
    }

}
