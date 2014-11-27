package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExternalCommand;

class RemoveCommand extends ExternalCommand {
    private Table table;

    public RemoveCommand(Table table) {
        super("remove", 1);
        this.table = table;
    }

    public void execute(String[] args, Shell shell) {
        if (table.exists(args[0])) {
            table.remove(args[0]);
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
};
