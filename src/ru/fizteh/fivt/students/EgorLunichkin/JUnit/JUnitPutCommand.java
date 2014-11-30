package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class JUnitPutCommand implements Command {
    public JUnitPutCommand(JUnitDataBase jdb, String key, String value) {
        this.key = key;
        this.value = value;
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;
    private String key;
    private String value;

    public void run() {

    }
}
