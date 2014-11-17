package ru.fizteh.fivt.students.EgorLunichkin.filemap;

public class ExitCommand implements Command {
    public ExitCommand() {}

    public void run() throws FileMapException {
        System.exit(0);
    }
}
