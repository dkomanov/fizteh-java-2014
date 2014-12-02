package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiGetCommand;

public class JUnitGetCommand implements JUnitCommand {
    public JUnitGetCommand(MyTableProvider mtp, String key) {
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
            String value = myTableProvider.getUsing().get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found\n" + value);
            }
        }
    }
}
