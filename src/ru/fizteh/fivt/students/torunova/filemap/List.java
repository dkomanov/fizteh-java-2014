package ru.fizteh.fivt.students.torunova.filemap;

import java.util.Set;

/**
 * Created by nastya on 08.10.14.
 */
public class List implements Action{
	@Override
	public boolean run(String[] args, Database db) {
		if(args.length > 0) {
			System.err.println("list:too many arguments.");
			return false;
		}
		Set<String> keys = db.list();
		for(String key:keys) {
			System.out.println(key);
		}
		return true;
	}

	@Override
	public String getName() {
		return "list";
	}
}
