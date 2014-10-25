package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExternalCommand;

class GetCommand extends ExternalCommand {
    private Table table;

    public GetCommand(Table table) {
        super("get", 1);
        this.table = table;
    }

    public void execute(String[] args, Shell shell) {
        if (table.exists(args[0])) {
            System.out.println("found");
            System.out.println(table.get(args[0]));
        } else {
            System.out.println("not found");
        }
    }
}
