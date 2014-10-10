package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 08 October 2014
 * Time: 21:41
 */
public class Exit {
    /**
     * Stop working with database.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    public static void exitRun(String[] currentArgs, final Map currentTable) {
        if (currentArgs.length != 1) {
            System.err.println("exit: too many arguments");
            System.exit(1);
        }
        WriteToFile.writeToFile(currentTable);
        System.exit(0);
    }
}
