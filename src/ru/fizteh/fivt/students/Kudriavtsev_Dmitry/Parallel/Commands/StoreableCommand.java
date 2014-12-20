package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Command;

import java.io.PrintStream;

/**
 * Created by Дмитрий on 26.11.2014.
 */
public abstract class StoreableCommand extends Command {
    PrintStream out;
    PrintStream err;

    public StoreableCommand(String name, int argLen) {
        this.name = name;
        this.argLen = argLen;
    }

    public StoreableCommand() {
        this(null, 0);
    }

    protected void noTable(PrintStream err) {
        err.println("No table");
        if (batchMode) {
            System.exit(-1);
        }
    }
}
