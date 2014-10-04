package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexeyZhuravlev
 */
public class GetCommand extends Command {

    private String key;

    public GetCommand(String passedKey) {
        key = passedKey;
    }

    @Override
    public void execute(DataBase base, AtomicBoolean exitStatus) {
        String value = base.data.get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
