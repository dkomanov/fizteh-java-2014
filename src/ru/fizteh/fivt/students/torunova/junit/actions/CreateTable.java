package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

import java.io.File;
import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class CreateTable extends Action{
    @Override
    public boolean run(String[] args, Database db)
                                throws IOException,
                                IncorrectFileException,
                                TableNotCreatedException {
		if (!checkNumberOfArguments(1, args.length)) {
			return false;
		}
        String tableName = args[0];
        if (tableName.contains(File.separator) || tableName.equals("..") || tableName.equals(".")) {
            throw new TableNotCreatedException("create: illegal name for table.");
        }
        if (db.createTable(tableName) != null) {
            System.out.println("created");
            return true;
        } else {
            System.out.println(tableName + " exists");
            return false;
        }
    }

    @Override
    public String getName() {
        return "create";
    }
}
