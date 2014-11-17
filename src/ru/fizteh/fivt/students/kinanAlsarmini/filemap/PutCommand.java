package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExternalCommand;

class PutCommand extends ExternalCommand {
    private Table table;

    public PutCommand(Table table) {
        super("put", 2);
        this.table = table;
    }

    public void execute(String[] args, Shell shell) {
        if (table.exists(args[0])) {
            System.out.println("overwrite");
            System.out.println(table.get(args[0]));
        } else {
            System.out.println("new");
        }

        table.put(args[0], args[1]);
    }
}
