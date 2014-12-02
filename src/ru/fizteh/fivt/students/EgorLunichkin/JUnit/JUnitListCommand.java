package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiListCommand;

public class JUnitListCommand implements JUnitCommand {
    public JUnitListCommand(MyTableProvider mtp) {
        this.myTableProvider = mtp;
    }

    private MyTableProvider myTableProvider;

    @Override
    public void run() {
       if (myTableProvider.getUsing() == null) {
           System.out.println("no table");
       } else {
           System.out.println(String.join(", ", myTableProvider.getUsing().list()));
       }
    }
}
