package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.commands;

import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class DropCommand extends Command {

    String name;

    @Override
    public void execute(StructuredTableProvider base) throws Exception {
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
