package ru.fizteh.fivt.students.torunova.proxy.actions;

import ru.fizteh.fivt.students.torunova.proxy.CurrentTable;
import java.io.IOException;

/**
 * Created by nastya on 02.11.14.
 */
public class Size extends Action {
    @Override
    public boolean run(String[] args, CurrentTable currentTable)
            throws IOException {
        if (!checkNumberOfArguments(0, args.length)) {
            return false;
        }
        if (currentTable.get() == null) {
            System.out.println("no table");
            return false;
        }
        System.out.println(currentTable.get().size());
        return true;
    }

    @Override
    public String getName() {
        return "size";
    }
}
