package ru.fizteh.fivt.students.torunova.multifilehashmap.actions;

import ru.fizteh.fivt.students.torunova.multifilehashmap.Database;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public class UseTable extends Action{
	@Override
	public boolean run(String[] args, Database db) throws IOException, IncorrectFileException {
		if(args.length > 1){
			tooManyArguments();
			return false;
		} else if(args.length < 1) {
			tooFewArguments();
			return false;
		}
		if(db.useTable(args[0])) {
			System.out.println("using " + args[0]);
			return true;
		} else {
			System.out.println(args[0] + "does not exist");
			return false;
		}
	}

	@Override
	public String getName() {
		return "use";
	}
}
