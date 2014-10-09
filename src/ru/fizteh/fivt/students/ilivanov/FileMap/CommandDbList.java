package ru.fizteh.fivt.students.ilivanov.FileMap;

import java.util.ArrayList;
import java.util.Set;

public class CommandDbList implements CommandDb {
    public CommandDbList(final ArrayList<String> parameters) throws Exception {
        if (parameters.size() != 1) {
            throw new Exception("wrong number of parameters");
        }
    }

    @Override
    public final int execute() {
        Set<String> keys = FileUsing.map.keySet();
        int size = keys.size();
        int i = 0;
        for (String key : keys) {
            i++;
            System.out.print(key);
            if (i < size) {
                System.out.print(", ");
            }
        }
        System.out.println("");
        return 0;
    }
}
