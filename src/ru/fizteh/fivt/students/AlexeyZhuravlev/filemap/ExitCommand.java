package ru.fizteh.fivt.students.AlexeyZhuravlev.filemap;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexeyZhuravlev
 */
public class ExitCommand extends Command {
    @Override
    public void execute(DataBase base, AtomicBoolean exitStatus) {
        exitStatus.set(true);
    }
}
