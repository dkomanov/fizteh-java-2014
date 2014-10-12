package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexeyZhuravlev
 */
public class ListCommand extends Command {

    public String getList(DataBase base) {
        StringBuilder allKeys = new StringBuilder();
        for (String key : base.data.keySet()) {
            if (allKeys.length() > 0) {
                allKeys.append(", ");
            }
            allKeys.append(key);
        }
        return allKeys.toString();
    }

    @Override
    public void execute(DataBase base, AtomicBoolean exitStatus) {
        System.out.println(getList(base));
    }

}
