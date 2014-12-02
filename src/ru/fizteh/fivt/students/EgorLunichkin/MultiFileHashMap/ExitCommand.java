package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class ExitCommand implements Command {
    public ExitCommand() {}

    public void run() {
        System.exit(0);
    }
    public void runOnTable(MultiTable table) {
        this.run();
    }
}
