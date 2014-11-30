package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class JUnitGetCommand implements Command {
    public JUnitGetCommand(JUnitDataBase jdb, String key) {
        this.key = key;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String key;

    public void run() {

    }
}
