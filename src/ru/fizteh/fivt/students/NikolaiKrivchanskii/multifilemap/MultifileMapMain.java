package ru.fizteh.fivt.students.NikolaiKrivchanskii.multifilemap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import ru.fizteh.fivt.students.NikolaiKrivchanskii.Shell.*;
import ru.fizteh.fivt.students.NikolaiKrivchanskii.filemap.*;

public class MultifileMapMain {
	public static void main(String args[]) {
		MultiFileMapShellState state = new MultiFileMapShellState();
		HashSet<Commands<FileMapShellState>> com =  new HashSet<Commands<FileMapShellState>>() {{ add(new ExitCommand()); add(new RollbackCommand()); add(new CommitCommand()); 
        add(new PutCommand()); add(new GetCommand()); add(new RemoveKeyCommand());}};
        HashSet<Commands<MultiFileMapShellState>> com1 =  new HashSet<Commands<MultiFileMapShellState>>() {{add(new DropCommand()); add(new UseCommand()); 
         add(new CreateCommand());}};
        ArrayList<Commands> res = new ArrayList<Commands>();
        res.addAll(com);
        res.addAll(com1);
        HashSet<Commands> actualResult = new HashSet<Commands>(res);
        Shell<MultiFileMapShellState> shell = new Shell<MultiFileMapShellState>(actualResult);
        String dbDirectory = System.getProperty("fizteh.db.dir");
        System.out.println(dbDirectory);
        DatabaseFactory factory = new DatabaseFactory();
        state.tableProvider = factory.create(dbDirectory);
        shell.setShellState(state);
        shell.consoleWay(state);
	    }
}
