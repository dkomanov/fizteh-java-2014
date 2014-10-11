package ru.fizteh.fivt.students.Bulat_Galiev.filemap;

import java.util.HashMap;
import java.util.Set;

public final class Filemapfunc {
    private Filemapfunc() {
        // Disable instantiation to this class.
    }

    public static void put(final Data link, final String arg1, final String arg2) {
        if ((!arg1.equals("")) && (!arg2.equals(""))) {
            HashMap<String, String> fileMap = link.getDataBase();
            String putValue = fileMap.put(arg1, arg2);
            if (putValue == null) {
                System.out.println("new");
            } else {
                System.out.println("overwrite");
                System.out.println(putValue);
            }
        } else {
            System.out.println("incorrect arguments");
            return;
        }
    }

    public static void get(final Data link, final String arg1) {
        if (!arg1.equals("")) {
            HashMap<String, String> fileMap = link.getDataBase();
            String getValue = fileMap.get(arg1);
            if (getValue == null) {
                System.out.println("not found");
            } else {
                System.out.println("found");
                System.out.println(getValue);
            }
        } else {
            System.out.println("incorrect arguments");
            return;
        }
    }

    public static void remove(final Data link, final String arg1) {
        if (!arg1.equals("")) {
            HashMap<String, String> fileMap = link.getDataBase();
            String getValue = fileMap.remove(arg1);
            if (getValue != null) {
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        } else {
            System.out.println("incorrect arguments");
            return;
        }
    }

    public static void list(final Data link) {
        HashMap<String, String> fileMap = link.getDataBase();
        Set<String> keys = fileMap.keySet();
        int iteration = 0;
        for (String current : keys) {
            iteration++;
            System.out.print(current);
            if (iteration != keys.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public static void exit(final Data link) {
        link.close();
    }

}
