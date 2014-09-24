package ru.fizteh.fivt.students.dsalnikov.filemap.commands;

import ru.fizteh.fivt.students.dsalnikov.filemap.Table;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;

import java.util.Set;

/**
 * Created by Dmitriy on 9/24/2014.
 */
public class ListCommand implements Command {

    Table db;

    public ListCommand(Table t) {
        db = t;
    }

    @Override
    public void execute(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("wrong amount of arguments");
        }
        Set<String> list = db.list();
        if (list.size() == 0) {
            System.out.println("");
        } else {
            for (String s : list) {
                System.out.println(s);
            }
        }
    }


    @Override
    public String getName() {
        return "list";
    }

    @Override
    public int getArgsCount() {
        return 1;
    }
}
