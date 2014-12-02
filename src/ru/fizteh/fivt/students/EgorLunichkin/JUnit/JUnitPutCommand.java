package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Command;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiPutCommand;

public class JUnitPutCommand implements JUnitCommand {
    public JUnitPutCommand(MyTableProvider mtp, String key, String value) {
        this.key = key;
        this.value = value;
        this.myTableProvider = mtp;
    }

    private MyTableProvider myTableProvider;
    private String key;
    private String value;

    @Override
    public void run() {
        if (myTableProvider.getUsing() == null) {
            System.out.println("no table");
        } else {
            String oldValue = myTableProvider.getUsing().put(key, value);
            if (oldValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite\n" + oldValue);
            }
        }
    }
}
