package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.Command;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.MyStoreableTableProvider;

import java.text.ParseException;

public class PutCommand extends Command {

    private String key;
    private String value;

    @Override
    public void execute(MyStoreableTableProvider base) throws Exception {
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
