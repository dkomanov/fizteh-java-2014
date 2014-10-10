package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 10 October 2014
 * Time: 22:36
 */
public class Get {
    /**
     * Get the value by having an object
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    public static void getRun(final String[] currentArgs, Map currentTable) {
        if (currentArgs.length != 2) {
            System.err.println("get: " + (currentArgs.length < 2 ? "not enough" : "too many") + " arguments");
            System.exit(1);
        }
        if (currentTable.containsKey(currentArgs[1])) {
            System.out.println("Found\nvalue = " + currentTable.get(currentArgs[1]));
        } else {
            System.out.println("Not found");
        }
    }
}
