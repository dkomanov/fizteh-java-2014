package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class CommitCommand implements Command {
    public CommitCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() {

    }
}
