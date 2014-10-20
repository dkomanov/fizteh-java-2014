package ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap;


import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;

public class FileMapMain {
    public static void main(String[] args) {
        FileMapShellState state = new FileMapShellState();
        Set<Commands> com =  new HashSet<Commands>() { { add(new ExitCommand());
            add(new RollbackCommand()); add(new CommitCommand()); 
            add(new PutCommand()); add(new GetCommand()); add(new RemoveKeyCommand());
            add(new ListCommand()); }};
        Shell<FileMapShellState> shell = new Shell<FileMapShellState>(com);
        String dbDirectory = System.getProperty("fizteh.db.dir");
        state.table = new SingleFileTable(dbDirectory, "master");
        shell.setShellState(state);
        shell.consoleWay(state);
    }
}
