package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap;

import ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap.Interpreter.InterpreterState;

public class DatabaseInterpreterState extends InterpreterState{
    Database database;

    public DatabaseInterpreterState(Database database) {
        this.database = database;
    }

    public Database getDatabase() {
        return database;
    }

    public boolean tryToSave() {
        try {
            save();
        } catch (TableNotFoundException ex) {
            //Table not selected.
        } catch (LoadOrSaveException | DatabaseFileStructureException ex) {
            System.err.println(ex.getMessage());
            return false;
        }
        return true;
    }

    public void save() throws LoadOrSaveException, TableNotFoundException, DatabaseFileStructureException {
        TableHash table = (TableHash) database.getUsingTable();
        if (table != null) {
            table.save();
        } else {
            throw new TableNotFoundException("Table isn't selected");
        }
    }
}
