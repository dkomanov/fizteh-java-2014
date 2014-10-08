package ru.fizteh.fivt.students.torunova.filemap;

/**
 * Created by nastya on 08.10.14.
 */
public class Put implements Action {
    @Override
    public boolean run(String[] args, Database db) {
        if (args.length < 2) {
            System.err.println("put:too few arguments.");
            return false;
        } else if (args.length > 2) {
            System.err.println("put:too many arguments.");
            return false;
        }
        String value = db.get(args[0]);
        boolean result = db.put(args[0], args[1]);
        if (result) {
            System.out.println("new");
        } else {
            System.out.println("overwrite\n" + value);
        }
        return true;
    }

    @Override
    public String getName() {
        return "put";
    }
}
