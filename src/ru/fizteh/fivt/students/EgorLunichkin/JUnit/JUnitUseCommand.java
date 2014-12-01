package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class JUnitUseCommand implements Command {
    public JUnitUseCommand(JUnitDataBase jdb, String name) {
        this.tableName = name;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String tableName;

    public void run() {

    }
}
