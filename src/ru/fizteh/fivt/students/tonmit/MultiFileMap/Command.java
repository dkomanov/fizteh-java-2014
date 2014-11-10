package ru.fizteh.fivt.students.tonmit.MultiFileMap;

public abstract class Command {

    protected String name;
    protected int argLen;
    protected boolean packageMode;

    public Command() {
        this.name = null;
        this.argLen = 0;
    }

    protected boolean checkArguments(int argLen) {
        if (argLen != this.argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            if (packageMode) {
                System.exit(-1);
            }
            return false;
        }
        return true;
    }

    protected void noTable() {
        System.err.println("No table");
        if (packageMode) {
            System.exit(-1);
        }
    }

    public abstract boolean exec(Connector dbConnector, String[] args);

    public final String toString() {
        return name;
    }

}
 