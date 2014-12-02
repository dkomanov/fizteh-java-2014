package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.fileMap;
import java.util.HashMap;

/**
 * Created by Дмитрий on 04.10.14.
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

    public abstract boolean exec(HashMap<String, String> dBase, String[] args);

    @Override
    public final String toString() {
        return name;
    }
}
