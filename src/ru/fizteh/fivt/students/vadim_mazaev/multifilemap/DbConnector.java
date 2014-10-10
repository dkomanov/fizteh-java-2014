package ru.fizteh.fivt.students.vadim_mazaev.multifilemap;

public final class DbConnector {
    public DbConnector() {
        //do nothing
    }
    
    TableManager create(String dir) {
        if (dir == null) {
            throw new IllegalArgumentException("Directory name is null");
        }
        return new TableManager(dir);
    }
}
