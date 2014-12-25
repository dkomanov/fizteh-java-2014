package ru.fizteh.fivt.students.dsalnikov.telnet;

import ru.fizteh.fivt.storage.structured.RemoteTableProvider;
import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.CreateCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.DropCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.UseCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StorableParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.storable.Database;
import ru.fizteh.fivt.students.dsalnikov.telnet.Commands.ClientExit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class NewWorkerRunnable implements Runnable {
    RemoteTableProvider provider;

    public NewWorkerRunnable(RemoteTableProvider provider) {
        this.provider = provider;
    }

    @Override
    public void run() {
        String workingDirectory = System.getProperty("fizteh.db.dir");
        Database state = new Database(new File(workingDirectory), provider);
        Shell sh = new Shell(new StorableParser());
        List<Command> commands = new ArrayList<>();
        commands.add(new PutCommand(state));
        commands.add(new ListCommand(state));
        commands.add(new RemoveCommand(state));
        commands.add(new GetCommand(state));
        commands.add(new ClientExit());
        commands.add(new CreateCommand(state));
        commands.add(new DropCommand(state));
        commands.add(new UseCommand(state));
        commands.add(new CommitCommand(state));
        commands.add(new RollbackCommand(state));
        commands.add(new SizeCommand(state));
        sh.setCommands(commands);
        sh.run(new String[0]);
    }
}
