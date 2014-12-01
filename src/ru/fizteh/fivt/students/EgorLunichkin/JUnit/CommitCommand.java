package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class CommitCommand implements JUnitCommand {
    public CommitCommand(JUnitDataBase jdb) {
        this.jUnitDataBase = jdb;
    }

    private JUnitDataBase jUnitDataBase;

    public void run() throws Exception {
        if (jUnitDataBase.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(jUnitDataBase.getUsing().commit());
        }
    }
}
