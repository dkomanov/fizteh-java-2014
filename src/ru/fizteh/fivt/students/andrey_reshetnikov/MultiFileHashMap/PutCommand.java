package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.concurrent.atomic.AtomicBoolean;

public class PutCommand extends CommandFileMap {
    private String key;
    private String value;

    public PutCommand(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    @Override
    public void execute(DataBaseOneFile base, AtomicBoolean exitStatus) throws Exception {
        String baseValue = base.data.get(key);
        if (baseValue != null) {
            System.out.println("overwrite");
            System.out.println(baseValue);
        } else {
            System.out.println("new");
        }
        base.data.put(key, value);
        base.dump();
    }
}
