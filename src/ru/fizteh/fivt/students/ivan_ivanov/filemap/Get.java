package ru.fizteh.fivt.students.ivan_ivanov.filemap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;

import java.io.IOException;

public class Get implements Command {

    public String getName() {
        return "get";
    }

    public void executeCmd(Shell filemap, String[] args) throws IOException {
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
