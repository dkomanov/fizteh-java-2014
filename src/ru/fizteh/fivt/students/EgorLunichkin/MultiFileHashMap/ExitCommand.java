package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class ExitCommand implements Command {
    public ExitCommand() {}

    public void run() throws MultiFileHashMapException {
        System.exit(0);
    }
}