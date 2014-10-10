package ru.fizteh.fivt.students.ivan_ivanov.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;

public class Put implements Command {

    public String getName() {
        return "put";
    }

    public void executeCmd(Shell filemap, String[] args) throws IOException {
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
