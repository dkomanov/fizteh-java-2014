package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Table;

public class ExitCommand implements Command {
    public ExitCommand() {}

    public void run() {
        System.exit(0);
    }
    public void runOnTable(Table table) {
        run();
    }
}
