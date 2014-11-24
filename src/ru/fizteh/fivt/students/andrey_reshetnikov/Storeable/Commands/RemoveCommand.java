package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

public class RemoveCommand extends Command {

    private String key;

    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            Storeable result = base.getUsing().remove(key);
            if (result == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
    }

    public RemoveCommand(){}

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    public void putArguments(String[] args) {
        key = args[1];
    }
}
