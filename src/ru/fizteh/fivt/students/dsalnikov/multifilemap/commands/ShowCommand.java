package ru.fizteh.fivt.students.dsalnikov.multifilemap.commands;

import ru.fizteh.fivt.students.dsalnikov.multifilemap.MultiTable;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.io.InputStream;
import java.io.PrintStream;

public class ShowCommand implements Command {

    MultiTable db;

    public ShowCommand(MultiTable t) {
        db = t;
    }

    @Override
    public void execute(String[] args, InputStream inputStream, PrintStream outputStream) throws Exception {
        if (!args[1].equals("tables")) {
            throw new IllegalArgumentException("wrong command: use show tables");
        } else {
            outputStream.println("table_name\trow_count");
            db.showTables().forEach(System.out::println);
        }
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
