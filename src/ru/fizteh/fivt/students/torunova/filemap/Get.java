package ru.fizteh.fivt.students.torunova.filemap;

/**
 * Created by nastya on 08.10.14.
 */
public class Get extends Action {
    @Override
    public boolean run(String[] args, Database db) {
        if (args.length < 1) {
          	tooFewArguments();
            return false;
        } else if (args.length > 1) {
           	tooManyArguments();
            return false;
        }
        String value = db.get(args[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
			System.out.println(value);
        }
        return true;
    }

    @Override
    public String getName() {
        return "get";
    }
}
