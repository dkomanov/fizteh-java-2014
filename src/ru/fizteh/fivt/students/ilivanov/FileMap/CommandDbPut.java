package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.util.ArrayList;

public class CommandDbPut implements CommandDb {
    private String key;
    private String value;

    public CommandDbPut(final ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 3) {
            throw new Exception("wrong number of parameters");
        } else {
            key = parameters.get(1);
            value = parameters.get(2);
        }
    }

    @Override
    public final int execute() {
        String oldValue = FileUsing.map.put(key, value);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
        return 0;
    }
}
