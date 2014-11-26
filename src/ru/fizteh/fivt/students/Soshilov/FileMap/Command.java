package ru.fizteh.fivt.students.Soshilov.FileMap;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 13 October 2014
 * Time: 18:51
 */
public interface Command {
    void execute(final String[] currentArgs, Map<String, String> currentTable);
}
