package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit.MyTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class JUseCommand extends JCommand {

    String tableName;

    @Override
    public void execute(MyTableProvider base) throws Exception {
        if (base.getUsing() != null && ((MyTable) base.getUsing()).unsavedChanges() != 0) {
            System.out.println(((MyTable) base.getUsing()).unsavedChanges() + " unsaved changes");
        } else {
            if (base.getTable(tableName) == null) {
                System.out.println(tableName + " not exists");
            } else {
                base.setUsing(tableName);
                System.out.println("using " + tableName);
            }
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        tableName = args[1];
    }
}
