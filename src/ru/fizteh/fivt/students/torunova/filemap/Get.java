package ru.fizteh.fivt.students.torunova.filemap;

/**
 * Created by nastya on 08.10.14.
 */
public class Get implements Action {
	@Override
	public boolean run(String[] args,Database db) {
		if(args.length < 1){
			System.err.println("get:too few arguments.");
			return false;
		} else if (args.length > 1) {
			System.err.println("get:too many arguments.");
			return false;
		}
		String value = db.get(args[0]);
		if(value == null) {
			System.out.println("not found");
		} else {
			System.out.println("found " + value);
		}
		return true;
	}
	@Override
	public String getName() {
		return "get";
	}
}
