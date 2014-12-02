package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class RollbackCommand implements JUnitCommand {
    public RollbackCommand(MyTableProvider mtp) {
        this.myTableProvider = mtp;
    }

    private MyTableProvider myTableProvider;

    @Override
    public void run() {
       if (myTableProvider.getUsing() == null) {
           System.out.println("no table");
       } else {
           System.out.println(myTableProvider.getUsing().rollback());
       }
    }
}
