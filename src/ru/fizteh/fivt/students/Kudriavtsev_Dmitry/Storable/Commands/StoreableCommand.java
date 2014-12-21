package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Command;

/**
 * Created by Дмитрий on 26.11.2014.
 */
public abstract class StoreableCommand extends Command {

    public StoreableCommand(String name, int argLen) {
        this.name = name;
        this.argLen = argLen;
    }

    public StoreableCommand() {
        this(null, 0);
    }

    protected void noTable() {
        System.err.println("No table");
        if (batchMode) {
            System.exit(-1);
        }
    }
}
