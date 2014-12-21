package ru.fizteh.fivt.students.egor_belikov.Storeable.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider;

import java.text.ParseException;

/**
 * Created by egor on 13.12.14.
 */
public class Put implements Command {
    @Override
    public void execute(String[] args, MyTableProvider myTableProvider) throws Exception {
        String arguments = args[2];
        if (args.length > 3) {
            for (int i = 3; i != args.length; i++) {
                arguments = arguments + " " + args[i];
            }
        }
        Storeable value = null;
        try {
            value = myTableProvider.deserialize(MyTableProvider.currentTable, arguments);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (MyTableProvider.currentTable.put(args[1], value) == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
        }
    }
}
