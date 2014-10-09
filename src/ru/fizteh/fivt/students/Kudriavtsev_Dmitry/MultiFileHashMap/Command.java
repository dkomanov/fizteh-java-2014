package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;


/**
 * Created by Дмитрий on 07.10.14.
 */
public abstract class Command {

    protected String name;
    protected int argLen;

    public Command(String name, int argLen) {
        this.name = name;
        this.argLen = argLen;
    }

    public Command() {
        this.name = null;
        this.argLen = 0;
    }

    public abstract boolean exec(Connector dbConnector, String[] args);

    @Override
    public final String toString() {
        return name;
    }

}
