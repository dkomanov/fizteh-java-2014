package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;

import java.io.IOException;

public class Remove implements Command {
    public final String getName() {
        return "remove";
    }
    public final void executeCmd(final Shell filemap, final String[] args) throws IOException {
        String key = args[0];
        String value = ((FileMap) filemap).getFileMapState().getDataBase().remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
