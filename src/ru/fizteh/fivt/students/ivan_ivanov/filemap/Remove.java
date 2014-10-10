package ru.fizteh.fivt.students.ivan_ivanov.filemap;

import java.io.IOException;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;

public class Remove implements Command {

    public String getName() {
        return "remove";
    }

    public void executeCmd(Shell filemap, String[] args) throws IOException {
        String key = args[0];
        String value = ((FileMap) filemap).getFileMapState().getDataBase().remove(key);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
