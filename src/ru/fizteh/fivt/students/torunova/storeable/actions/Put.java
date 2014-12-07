package ru.fizteh.fivt.students.torunova.storeable.actions;

import ru.fizteh.fivt.students.torunova.storeable.CurrentTable;
import ru.fizteh.fivt.students.torunova.storeable.StoreableType;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by nastya on 21.10.14.
 */
public class Put extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable) throws IOException {
        String[] arguments = new String[2];
        arguments[0] = args[0];
        arguments[1] = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        if (!checkNumberOfArguments(2, arguments.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        String oldValue;
        StoreableType deserializedValue = null;
        try {
            deserializedValue = (StoreableType) currentTable.getDb().deserialize(currentTable.get(), arguments[1]);
        } catch (ParseException e) {
            //it is never thrown.
        }
        oldValue = currentTable.getDb().serialize(currentTable.get(),
                currentTable.get().put(args[0], deserializedValue));
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
        return true;
    }

    @Override
    public String getName() {
        return "put";
    }
}
