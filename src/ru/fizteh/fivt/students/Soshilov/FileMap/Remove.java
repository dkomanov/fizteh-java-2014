package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 10 October 2014
 * Time: 22:42
 */
public class Remove implements Command {
    /**
     * Remove key + value by getting the key as argument.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    @Override
    public void execute(final String[] currentArgs, Map<String, String> currentTable) {
        FileMapRun.checkArguments(currentArgs.length, 2);
        if (currentTable.remove(currentArgs[1]) != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
