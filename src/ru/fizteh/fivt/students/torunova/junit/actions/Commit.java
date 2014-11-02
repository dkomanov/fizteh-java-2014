package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 01.11.14.
 */
public class Commit extends Action {
	@Override
	public boolean run(String[] args, Database db) throws IOException, IncorrectFileException, TableNotCreatedException {
		if (checkNumberOfArguments(0,args.length)) {
			if (db.currentTable == null) {
				System.err.println("no table");
				return false;
			}
			System.out.println(db.currentTable.commit());
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "commit";
	}
}
