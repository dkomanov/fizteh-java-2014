package ru.fizteh.fivt.students.anastasia_ermolaeva.filemap;

import java.util.Iterator;
import java.util.Set;

public final class Commands {
    private Commands() {
        //
    }

    public static void main(final String[] args) {

    }

    public static void put(
            final DbOperations db, final String[] args) {
        String command = "put";
        if (args.length > 3) {
            throw new IllegalArgumentException(
                    command + ": too much arguments");
        } else {
            if (args.length < 3) {
                throw new IllegalArgumentException(
                        command + ": missing operand");
            } else {
                if (!args[1].isEmpty() && !args[2].isEmpty()) {
                    String key = args[1];
                    String value = args[2];
                    if (!db.getDataBase().containsKey(key)) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                        System.out.println(db.getDataBase().get(key));
                        db.getDataBase().remove(key);
                    }
                    db.getDataBase().put(key, value);
                }
            }
        }
    }

    public static void get(
            final DbOperations db, final String[] args) {
        String command = "get";
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    command + ": too much arguments");
        } else {
            if (args.length < 2) {
                throw new IllegalArgumentException(
                        command + ": missing operand");
            } else {
                if (!args[1].isEmpty()) {
                    String key = args[1];
                    if (!db.getDataBase().containsKey(key)) {
                        System.out.println("found");
                        System.out.println(db.getDataBase().get(key));
                    } else {
                        System.out.println("not found");
                    }
                }
            }
        }
    }

    public static void remove(
            final DbOperations db, final String[] args) {
        String command = "remove";
        if (args.length > 2) {
            throw new IllegalArgumentException(
                    command + ": too much arguments");
        } else {
            if (args.length < 2) {
                throw new IllegalArgumentException(
                        command + ": missing operand");
            } else {
                if (!args[1].isEmpty()) {
                    String key = args[1];
                    if (!db.getDataBase().containsKey(key)) {
                        db.getDataBase().remove(key);
                        System.out.println("removed");
                    } else {
                        System.out.println("not found");
                    }
                }
            }
        }
    }

    public static void list(
            final DbOperations db, final String[] args) {
        String command = "list";
        if (args.length > 1) {
            throw new IllegalArgumentException(
                    command + ": too much arguments");
        }
        Set keySet = db.getDataBase().keySet();
        Iterator it = keySet.iterator();
        while (it.hasNext()) {
            System.out.print(it.next() + ", ");
        }
        System.out.println();
    }

    public static boolean exit(
            final DbOperations db, final String[] args) {
        if (args.length > 1) {
            return false;
        }
        db.close();
        System.out.println("exit");
        return true;
    }
}
