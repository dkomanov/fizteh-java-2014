package ru.fizteh.fivt.students.torunova.multifilehashmap.actions;

import ru.fizteh.fivt.students.torunova.multifilehashmap.Database;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class CreateTable extends Action{
	@Override
	public boolean run(String[] args, Database db) throws IOException, IncorrectFileException,TableNotCreatedException {
		if(args.length > 1) {
			tooManyArguments();
			return false;
		} else if(args.length < 1) {
			tooFewArguments();
			return false;
		}
		if(db.createTable(args[0])) {
			System.out.println("created");
			return true;
		} else {
			System.out.println(args[0] + " exists");
			return false;
		}
	}

	@Override
	public String getName() {
		return "create";
	}
}
