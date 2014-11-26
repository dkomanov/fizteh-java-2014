package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExternalCommand;
import java.util.Set;

class ListCommand extends ExternalCommand {
    private Table table;

    public ListCommand(Table table) {
        super("list", 0);
        this.table = table;
    }

    public void execute(String[] args, Shell shell) {
        Set<String> keys = table.keySet();
        StringBuilder keysConcatenation = new StringBuilder();

        for (String key : keys) {
            keysConcatenation.append(key + " ");
        }

        System.out.println(keysConcatenation.toString());
    }
}
