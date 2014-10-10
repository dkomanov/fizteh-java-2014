package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 10 October 2014
 * Time: 22:42
 */
public class Remove {
    /**
     * Remove key + value by getting the key as argument.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    public static void removeRun(final String[] currentArgs, Map currentTable) {
        if (currentArgs.length != 2) {
            System.err.println("remove:" + (currentArgs.length < 2 ? "not enough" : "too many") + " arguments");
            System.exit(1);
        }
        if (currentTable.remove(currentArgs[1]) != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
