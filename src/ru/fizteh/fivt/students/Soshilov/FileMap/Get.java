package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 10 October 2014
 * Time: 22:36
 */
public class Get implements Command {
    /**
     * Get the value by having an object.
     * @param currentArgs Commands that were entered: name, its arguments.
     * @param currentTable Our main table.
     */
    @Override
    public void execute(String[] currentArgs, Map<String, String> currentTable) {
        FileMapRun.checkArguments(currentArgs.length, 2);
        if (currentTable.containsKey(currentArgs[1])) {
            System.out.println("Found\nvalue = " + currentTable.get(currentArgs[1]));
        } else {
            System.out.println("Not found");
        }
    }
}
