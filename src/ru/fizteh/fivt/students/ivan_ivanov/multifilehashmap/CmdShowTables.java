package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Shell;

import java.io.IOException;
import java.util.Set;

public class CmdShowTables implements Command {

    @Override
    public final String getName() {

        return "show";
    }

    @Override
    public final void executeCmd(final Shell shell, final String[] args) throws IOException {
        if (args.length != 1) {
            throw new IOException("Can't find key");
        }

        if (!args[0].equals("tables")) {
            throw new IOException("Can't find key");
        }

        System.out.println("table_name row_count");

        Set<String> tables = ((MultiFileHashMap) shell).getMFHMState().getTableSet();
        for (String table : tables) {
            System.out.println(String.join(" ", table, String.valueOf(((MultiFileHashMap) shell).
                    getMFHMState().getTable(table).getDataBase().keySet().size())));
        }
    }
}
