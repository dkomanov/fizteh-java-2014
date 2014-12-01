package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class JUnitDropCommand implements Command {
    public JUnitDropCommand(JUnitDataBase jdb, String name) {
        this.tableName = name;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String tableName;

    public void run() {

    }
}
