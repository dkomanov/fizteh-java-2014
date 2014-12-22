package ru.fizteh.fivt.students.LebedevAleksey.MultiFileHashMap;

public class DatabaseState extends InterpreterState {
    protected Database database;

    public DatabaseState() throws DatabaseFileStructureException, LoadOrSaveException {
        super();
        String directoryPath = System.getProperty("fizteh.db.dir");
        if (directoryPath == null) {
            throw new DatabaseFileStructureException("Database directory doesn't set");
        } else {
            database = new Database(directoryPath);
        }
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
        Table table = database.getCurrentTable();
        if (table != null) {
            table.save();
        } else {
            throw new TableNotFoundException("Table isn't selected");
        }
    }
}
