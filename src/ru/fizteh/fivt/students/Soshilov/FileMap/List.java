package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 10 October 2014
 * Time: 22:51
 */
public class List {
    /**
     * List whole table.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    public static void listRun(final String[] currentArgs, final Map<String, String> currentTable) {
        FileMapRun.checkArguments(currentArgs.length, 1);
        Set<String> set = currentTable.keySet();
        for (String s : set) {
            System.out.print(s + ", ");
        }
        System.out.println();
    }
}
