package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.concurrent.atomic.AtomicBoolean;

public class RemoveCommand extends CommandFileMap {

    private String key;

    public RemoveCommand(String passedKey) {
        key = passedKey;
    }

    @Override
    public void execute(DataBaseOneFile base, AtomicBoolean exitStatus) throws Exception {
        if (base.data.containsKey(key)) {
            base.data.remove(key);
            base.dump();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
