package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class RollbackCommand implements Command {
    public RollbackCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() {

    }
}
