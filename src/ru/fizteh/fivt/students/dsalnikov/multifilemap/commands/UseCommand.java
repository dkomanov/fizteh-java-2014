package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;

import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.File;

public class UseCommand implements Command {

    MultiTable db;

    public UseCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {

        int changes = db.getAmountOfChanges();
        if (changes > 0) {
            System.out.println("Error: there are " + changes + " unsaved changes");
        } else {
            File file = new File(db.getDbFile(), args[1]);
            if (!file.exists()) {
                System.out.println(String.format("%s not exists", file.getName()));
            } else {
                db.use(args[1]);
                System.out.println("using " + file.getName());
            }
        }
    }

    @Override
    public String getName() {
        return "use";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
