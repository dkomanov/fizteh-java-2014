package ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.table;

import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.DataBaseShell;
import ru.fizteh.fivt.students.akhtyamovpavel.multifilemap.commands.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by user1 on 07.10.2014.
 */
public abstract class TableCommand implements Command {
    protected DataBaseShell shell;
    public static final boolean EXIST = true;
    public static final boolean NON_EXIST = false;

    public TableCommand(DataBaseShell shell) {
        this.shell = shell;
    }

    public boolean onExistCheck(String name, boolean existMode) {
        Path newPath = Paths.get(shell.getDataBaseDirectory().toString(), name);
        if (!Files.exists(newPath) && existMode) {
            System.out.println(name + " not exists");
            return false;
        }
        if (Files.exists(newPath) && !existMode) {
            System.out.println(name + " exists");
            return false;
        }
        return true;
    }

    public DataBaseShell getShell() {
        return shell;
    }
}
