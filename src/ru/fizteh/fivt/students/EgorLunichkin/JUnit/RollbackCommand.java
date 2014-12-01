package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class RollbackCommand implements JUnitCommand {
    public RollbackCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() {
        if (jUnitDataBase.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(jUnitDataBase.getUsing().rollback());
        }
    }
}
