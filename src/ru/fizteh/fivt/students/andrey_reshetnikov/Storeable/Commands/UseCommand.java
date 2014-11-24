package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

public class UseCommand extends Command {

    String name;

    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
        if (base.getUsing() != null && ((MyStoreableTable) base.getUsing()).unsavedChanges() != 0) {
            System.out.println(((MyStoreableTable) base.getUsing()).unsavedChanges() + " unsaved changes");
        } else if (base.setUsing(name) == null) {
            System.out.println(name + " not exists");
        } else {
            System.out.println("using " + name);
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    protected void putArguments(String[] args) {
        name = args[1];
    }
}
