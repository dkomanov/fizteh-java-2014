package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;

/**
 * @author AlexeyZhuravlev
 */
public class GetCommand extends Command {

    String key;

    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            Storeable value = base.getUsing().get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(base.serialize(base.getUsing(), value));
            }
        }
    }

    @Override
    protected int numberOfArguments() {
        return 1;
    }

    @Override
    public void putArguments(String[] args) {
        key = args[1];
    }
}
