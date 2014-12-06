package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Shell;
import ru.fizteh.fivt.students.AlexanderKhalyapov.Shell.Command;

import java.io.IOException;
import java.util.Set;

public class List implements Command {
    public final String getName() {
        return "list";
    }
    public final void executeCmd(final Shell filemap, final String[] args) throws IOException {
        Set<String> keys = ((FileMap) filemap).getFileMapState().getDataBase().keySet();
        System.out.println(String.join(", ", keys));
    }
}
