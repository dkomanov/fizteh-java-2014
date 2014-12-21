package ru.fizteh.fivt.students.SibgatullinDamir.storeable;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class RollbackJUnitCommand implements Commands {
    public void execute(String[] args, MyTable table) throws MyException {

        if (args.length == 1) {
            if (table.currentTable != null) {
                table.currentTable.clear();
                table.currentTable.putAll(table.committedTable);
                System.out.println(table.changedKeys.size());
                table.changedKeys.clear();
            } else {
                throw new MyException("no table");
            }
        } else {
            throw new MyException("rollback: too many arguments");
        }

    }

    public String getName() {
        return "rollback";
    }
}
