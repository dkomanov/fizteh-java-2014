package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.IOException;

public class Put implements Command {
    public final String getName() {
        return "put";
    }
    public final void executeCmd(final Shell filemap, final String[] args) throws IOException {
        String key = args[0];
        String value = args[1];
        String oldValue = ((FileMap) filemap).getFileMapState().getDataBase().put(key, value);
        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
