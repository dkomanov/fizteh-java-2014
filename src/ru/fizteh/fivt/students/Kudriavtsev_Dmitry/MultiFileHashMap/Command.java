package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;


/**
 * Created by Дмитрий on 07.10.14.
 */
public abstract class Command {

    protected String name;
    protected int argLen;
    protected boolean batchMode;
    protected boolean batchModeInInteractive;

    public Command(String name, int argLen) {
        this.name = name;
        this.argLen = argLen;
    }

    public Command() {
        this.name = null;
        this.argLen = 0;
    }

    protected boolean checkArguments(int argLen) {
        if (argLen != this.argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            if (batchMode) {
                System.exit(-1);
            }
            return false;
        }
        return true;
    }

    protected void noTable() {
        System.err.println("No table");
        if (batchMode) {
            System.exit(-1);
        }
    }

    public abstract boolean exec(Connector dbConnector, String[] args);

    @Override
    public final String toString() {
        return name;
    }

}
