package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands;

/**
 * @author AlexeyZhuravlev
 */
public class ShowTablesTableCommand extends TableCommand {
    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        for (String name: base.getTableNames()) {
            System.out.println(name + " " + base.getTable(name).size());
        }
    }

    @Override
    protected int numberOfArguments() {
        return 0;
    }
}
