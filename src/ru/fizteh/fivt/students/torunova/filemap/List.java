package ru.fizteh.fivt.students.torunova.filemap;

import java.util.Set;

/**
 * Created by nastya on 08.10.14.
 */
public class List extends Action {
    @Override
    public boolean run(String[] args, Database db) {
        if (args.length > 0) {
            tooManyArguments();
            return false;
        }
        Set<String> keys = db.list();
		String result = String.join(", ", keys);
   		System.out.println(result);
        return true;
    }

    @Override
    public String getName() {
        return "list";
    }
}
