package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class CommitCommand implements JUnitCommand {
    public CommitCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() {

    }
}
