package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiRemoveCommand;

public class JUnitRemoveCommand implements JUnitCommand {
    public JUnitRemoveCommand(MyTableProvider mtp, String key) {
        this.key = key;
        this.myTableProvider = mtp;
    }

    private MyTableProvider myTableProvider;
    private String key;

    @Override
    public void run() {
       if (myTableProvider.getUsing() == null) {
           System.out.println("no table");
       } else {
           String value = myTableProvider.getUsing().remove(key);
           if (value == null) {
               System.out.println("not found");
           } else {
               System.out.println("removed");
           }
       }
    }
}
