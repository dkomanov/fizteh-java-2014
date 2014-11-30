package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 08 October 2014
 * Time: 21:41
 */
public class Exit implements Command {
    /**
     * Stop working with database.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    @Override
    public void execute(String[] currentArgs, Map<String, String> currentTable) {
        FileMapRun.checkArguments(currentArgs.length, 1);
        WriteToFile.writeToFile(currentTable);
        System.exit(0);
    }
}
