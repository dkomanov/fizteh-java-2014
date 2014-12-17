package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class DropTable extends Action {
    TableHolder currentTable;
    public DropTable(TableHolder currentTable) {
        this.currentTable = currentTable;
    }
    @Override
    public boolean run(String args) throws IOException {
        String[] parameters = parseArguments(args);
        if (!checkNumberOfArguments(1, parameters.length)) {
            return false;
        }
        try {
            currentTable.getDb().removeTable(parameters[0]);
        } catch (IllegalStateException e) {
            System.err.println(e.getMessage());
            return false;
        }
        if (currentTable.get() != null) {
            if (currentTable.get().getName().equals(parameters[0])) {
                currentTable.reset();
            }
        }
        System.out.println("dropped");
        return true;
    }

    @Override
    public String getName() {
        return "drop";
    }
}
