package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class CreateCommand extends JCommand {
    private String tableName;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.createTable(tableName) == null) {
            System.out.println(tableName + " exists");
        } else {
            System.out.println("created");
        }
    }

    protected final void putArguments(String[] args) {
        tableName = args[1];
    }

    protected final int numberOfArguments() {
        return 1;
    }
}
