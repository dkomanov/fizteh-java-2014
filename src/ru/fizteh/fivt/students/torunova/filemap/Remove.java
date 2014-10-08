package ru.fizteh.fivt.students.torunova.filemap;

/**
 * Created by nastya on 09.10.14.
 */
public class Remove implements Action {
    @Override
    public boolean run(String[] args, Database db) {
        if (args.length < 1) {
            System.err.println("remove:too few arguments.");
            return false;
        } else if (args.length > 1) {
            System.err.println("remove:too many arguments.");
            return false;
        }
        boolean result = db.remove(args[0]);
        if (result) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }

    @Override
    public String getName() {
        return "remove";
    }
}
