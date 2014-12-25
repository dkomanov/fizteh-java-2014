package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;

import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.AbstractCommand;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;

public class UseCommand extends AbstractCommand {

    MultiTable db;

    public UseCommand(MultiTable t) {
        super("use", 1);
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {

        int changes = db.getAmountOfChanges();
        if (changes > 0) {
            outputStream.println("Error: there are " + changes + " unsaved changes");
        } else {
            File file = new File(db.getDbFile(), args[1]);
            if (!file.exists()) {
                outputStream.println(String.format("%s not exists", file.getName()));
            } else {
                db.use(args[1]);
                outputStream.println("using " + file.getName());
            }
        }
    }
}

