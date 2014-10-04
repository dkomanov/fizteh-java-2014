package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexeyZhuravlev
 */
public class ListCommand extends Command {
    @Override
    public void execute(DataBase base, AtomicBoolean exitStatus) {
        StringBuilder allKeys = new StringBuilder();
        for (String key : base.data.keySet()) {
            if (allKeys.length() > 0) {
                allKeys.append(", ");
            }
            allKeys.append(key);
        }
        System.out.println(allKeys.toString());
    }
}
