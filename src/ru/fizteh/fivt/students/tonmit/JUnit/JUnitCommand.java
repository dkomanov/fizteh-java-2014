package ru.fizteh.fivt.students.tonmit.JUnit;

public abstract class JUnitCommand {

    protected String name;
    protected int argLen;
    protected boolean batchMode;
    protected boolean batchModeInInteractive;

    public JUnitCommand(String name, int argLen) {
        this.name = name;
        this.argLen = argLen;
    }

    public JUnitCommand() {
        this(null, 0);
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

    public abstract boolean exec(Connector dbConnector, String[] args);

    protected void noTable() {
        System.err.println("No table");
        if (batchMode) {
            System.exit(-1);
        }
    }
}
