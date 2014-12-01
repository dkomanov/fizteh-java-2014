package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public abstract class JUnitCommand extends Command {

    public JUnitCommand(String name, int argLen) {
        this.name = name;
        this.argLen = argLen;
    }

    public JUnitCommand() {
        this(null, 0);
    }

    protected void noTable() {
        System.err.println("No table");
        if (batchMode) {
            System.exit(-1);
        }
    }
}
