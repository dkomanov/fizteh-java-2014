package ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.Command;
import ru.fizteh.fivt.students.AlexeyZhuravlev.storeable.StructuredTableProvider;

import java.text.ParseException;

/**
 * @author AlexeyZhuravlev
 */
public class PutCommand extends Command {

    String key;
    String value;

    @Override
    public void execute(StructuredTableProvider base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            try {
                Storeable storeable = base.deserialize(base.getUsing(), value);
                Storeable old = base.getUsing().put(key, storeable);
                if (old == null) {
                    System.out.println("new");
                } else {
                    System.out.println("overwrite");
                    System.out.println(base.serialize(base.getUsing(), old));
                }
            } catch (ParseException e) {
                throw new Exception("wrong type (" + e.getMessage() + ")");
            }
        }
    }

    @Override
    protected int numberOfArguments() {
        return 2;
    }

    @Override
    protected void putArguments(String[] args) {
        key = args[1];
        value = args[2].replaceAll("`", " ");
    }
}
