package ru.fizteh.fivt.students.torunova.storeable.database.actions;

import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Exit extends Action {
    TableHolder currentTable;
    public Exit(TableHolder currentTable) {
        this.currentTable = currentTable;
    }

    @Override
    public boolean run(String args) {
        String[] parameters = parseArguments(args);

        if (currentTable.get() != null) {
            currentTable.get().commit();
        }
        if (parameters.length == 1) {
            System.exit(getStatus(args));
        } else {
            System.exit(0);
        }
        return true;
    }
    @Override
    public String getName() {
        return "exit";
    }
    private int getStatus(String args) {
        return Integer.parseInt(args);
    }
}
