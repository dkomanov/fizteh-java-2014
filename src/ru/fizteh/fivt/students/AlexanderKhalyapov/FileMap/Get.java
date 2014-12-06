package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;

import java.io.IOException;

public class Get implements Command {
    public final String getName() {
        return "get";
    }
    public final void executeCmd(final Shell filemap, final String[] args) throws IOException {
        String key = args[0];
        String value = ((FileMap) filemap).getFileMapState().getDataBase().get(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(value);
        }
    }
}
