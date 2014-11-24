package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

public class DropCommand extends Command {

    private String name;

    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
        try {
            base.removeTable(name);
            System.out.println("dropped");
        } catch (IllegalStateException e) {
            System.out.println(name + " not exists");
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
