package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 08 October 2014
 * Time: 21:37
 */
public class Put {
    /**
     * Put a key + value into a table.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    public static void putRun(String[] currentArgs, final Map<String, String> currentTable) {
        FileMapRun.checkArguments(currentArgs.length, 3);
        if (currentTable.containsKey(currentArgs[1])) {
            System.out.println("overwrite\n" + "old value = " + currentTable.get(currentArgs[1]));
        } else {
            System.out.println("new");
        }
        try {
            currentTable.put(currentArgs[1], currentArgs[2]);
        } catch (Throwable e) {
            System.err.println("cannot put a value");
            System.exit(1);
        }
        WriteToFile.writeToFile(currentTable);
    }
}
