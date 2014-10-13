package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 10 October 2014
 * Time: 22:51
 */
public class List implements Command {
    /**
     * List whole table.
     * @param currentArgs Commands that were entered: name, its' arguments.
     * @param currentTable Our main table.
     */
    @Override
    public void execute(final String[] currentArgs, final Map<String, String> currentTable) {
        FileMapRun.checkArguments(currentArgs.length, 1);
        Set<String> set = currentTable.keySet();
        int counter = 0;
        for (String s : set) {
            ++counter;
            if (counter == set.size()) {
                System.out.print(s + ", ");
            } else {
                System.out.print(s);
            }

        }

        System.out.println();
    }
}
