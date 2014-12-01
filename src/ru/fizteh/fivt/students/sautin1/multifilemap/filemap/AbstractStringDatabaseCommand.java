package ru.fizteh.fivt.students.sautin1.multifilemap.filemap;

import ru.fizteh.fivt.students.sautin1.multifilemap.shell.AbstractCommand;

/**
 *
 * Created by sautin1 on 10/12/14.
 */
public abstract class AbstractStringDatabaseCommand extends AbstractCommand<StringDatabaseState> {

    public AbstractStringDatabaseCommand(String name, int minArgNumber, int maxArgNumber) {
        super(name, minArgNumber, maxArgNumber);
    }
}
