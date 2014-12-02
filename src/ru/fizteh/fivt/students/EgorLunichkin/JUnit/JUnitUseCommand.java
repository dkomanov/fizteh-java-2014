package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class JUnitUseCommand implements JUnitCommand {
    public JUnitUseCommand(MyTableProvider mtp, String name) {
        this.tableName = name;
        this.myTableProvider = mtp;
    }

    private MyTableProvider myTableProvider;
    private String tableName;

    @Override
    public void run() {
       if (myTableProvider.getUsing() != null && ((MyTable) myTableProvider.getUsing()).unsavedChanges() > 0) {
           System.out.println(((MyTable) myTableProvider.getUsing()).unsavedChanges() + " unsaved changes");
       } else {
           if (myTableProvider.getTable(tableName) == null) {
               System.out.println(tableName + " not exists");
           } else {
               myTableProvider.using = tableName;
               System.out.println("using " + tableName);
           }
       }
    }
}
