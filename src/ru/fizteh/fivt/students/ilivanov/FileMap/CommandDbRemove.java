package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.util.ArrayList;

public class CommandDbRemove implements CommandDb {
    private String key;

    public CommandDbRemove(final ArrayList<String> parameters)
            throws Exception {
        if (parameters.size() != 2) {
            throw new Exception("wrong number of parameters");
        } else {
            key = parameters.get(1);
        }
    }

    @Override
    public final int execute() {
        String value = FileUsing.map.remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }

        return 0;
    }
}
