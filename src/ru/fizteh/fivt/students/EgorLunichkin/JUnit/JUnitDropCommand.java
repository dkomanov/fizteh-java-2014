package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class JUnitDropCommand implements JUnitCommand {
    public JUnitDropCommand(MyTableProvider mtp, String name) {
        this.tableName = name;
        this.myTableProvider = mtp;
    }

    private MyTableProvider myTableProvider;
    private String tableName;

    @Override
    public void run() {
        try {
            myTableProvider.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException ex) {
            System.out.println(tableName + "not exists");
        }
    }
}
