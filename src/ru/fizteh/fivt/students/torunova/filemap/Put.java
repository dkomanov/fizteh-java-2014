package ru.fizteh.fivt.students.torunova.filemap;

/**
 * Created by nastya on 08.10.14.
 */
public class Put extends Action {
    @Override
    public boolean run(String[] args, Database db) {
        if (args.length < 2) {
            tooFewArguments();
            return false;
        } else if (args.length > 2) {
            tooManyArguments();
            return false;
        }
        String value = db.get(args[0]);
        boolean result = db.put(args[0], args[1]);
        if (result) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
			System.out.println(value);
        }
        return true;
    }

    @Override
    public String getName() {
        return "put";
    }
}
