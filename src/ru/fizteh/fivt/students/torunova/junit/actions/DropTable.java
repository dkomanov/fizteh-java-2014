package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class DropTable extends Action {
    @Override
    public boolean run(String[] args, Database db) throws IOException, IncorrectFileException {
        if (!checkNumberOfArguments(1, args.length)) {
            return false;
        }
        if (db.dropTable(args[0])) {
            System.out.println("dropped");
            return true;
        } else {
            System.out.println(args[0] + " does not exist");
            return false;
        }
    }

    @Override
    public String getName() {
        return "drop";
    }
}
