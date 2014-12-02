package ru.fizteh.fivt.students.SibgatullinDamir.storable;

/**
 * Created by Lenovo on 10.11.2014.
 */
public class CommitJUnitCommand implements Commands {

    public void execute(String[] args, MyTable table) throws MyException {

        if (args.length == 1) {
            if (table.currentTable != null) {
                table.currentTable.write(table.getLocation());
                System.out.println(table.changedKeys.size());
                table.changedKeys.clear();
                table.committedTable.clear();
                table.committedTable.putAll(table.currentTable);
            } else {
                throw new MyException("no table");
            }
        } else {
            throw new MyException("commit: too many arguments");
        }

    }

    public String getName() {
        return "commit";
    }
}
