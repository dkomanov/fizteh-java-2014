package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexeyZhuravlev
 */
public class RemoveCommand extends Command {

    private String key;

    public RemoveCommand(String passedKey) {
        key = passedKey;
    }

    @Override
    public void execute(DataBase base, AtomicBoolean exitStatus) throws Exception {
        if (base.data.containsKey(key)) {
            base.data.remove(key);
            base.sync();
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
