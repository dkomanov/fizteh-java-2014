package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;
import ru.fizteh.fivt.students.torunova.storeable.database.StoreableType;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Created by nastya on 21.10.14.
 */
public class Put extends Action {
    @Override
    public boolean run(String args, TableHolder currentTable) throws IOException {
        String[] arguments = new String[2];
        String[] parameters = parseArguments(args);
        arguments[0] = parameters[0];
        arguments[1] = String.join(" ", Arrays.copyOfRange(parameters, 1, parameters.length));
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
                currentTable.get().put(parameters[0], deserializedValue));
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
