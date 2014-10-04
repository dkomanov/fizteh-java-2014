package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexeyZhuravlev
 */
public class PutCommand extends Command {
    private String key;
    private String value;

    public PutCommand(String passedKey, String passedValue) {
        key = passedKey;
        value = passedValue;
    }

    @Override
    public void execute(DataBase base, AtomicBoolean exitStatus) throws Exception {
        String baseValue = base.data.get(key);
        if (baseValue != null) {
            System.out.println("overwrite");
            System.out.println(baseValue);
        } else {
            System.out.println("new");
        }
        base.data.put(key, value);
        base.sync();
    }
}
