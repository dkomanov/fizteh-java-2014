package ru.fizteh.fivt.students.MaximGotovchits.JUnit;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public abstract class CommandTools {
    static String dataBaseName = System.getProperty("fizteh.db.dir");
    static Map<String, String> storage = new HashMap<String, String>();
    static Map<String, String> commitStorage = new HashMap<String, String>();
    static Stack lastChanges = new Stack();
    static int longestName = 260;
    static int uncommitedChanges = 0;
    static Boolean tableIsChosen = false; // Indicates if work table is chosen.
}
