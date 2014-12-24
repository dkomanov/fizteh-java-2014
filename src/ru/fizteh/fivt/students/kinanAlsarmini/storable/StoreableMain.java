package ru.fizteh.fivt.students.kinanAlsarmini.storable;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands.*;
import ru.fizteh.fivt.students.kinanAlsarmini.multifilemap.commands.*;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.Command;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.HelpCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseTableProviderFactory;
import ru.fizteh.fivt.students.kinanAlsarmini.storable.database.DatabaseTableProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StoreableMain {
    public static void main(String[] args) {
        Shell<StoreableShellState> shell = new Shell<StoreableShellState>();

        List<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(new PutCommand<Table, String, Storeable, StoreableShellState>());
        commands.add(new GetCommand<Table, String, Storeable, StoreableShellState>());
        commands.add(new RemoveCommand<Table, String, Storeable, StoreableShellState>());
        commands.add(new ListCommand<Table, String, Storeable, StoreableShellState>());
        commands.add(new CommitCommand<StoreableShellState>());
        commands.add(new RollbackCommand<StoreableShellState>());
        commands.add(new ExitCommand<StoreableShellState>());
        commands.add(new CreateCommand<Table, String, Storeable, StoreableShellState>());
        commands.add(new DropCommand<StoreableShellState>());
        commands.add(new UseCommand<Table, String, Storeable, StoreableShellState>());
        commands.add(new ShowCommand<StoreableShellState>());
        commands.add(new HelpCommand<StoreableShellState>(commands));

        shell.setCommands(commands);

        String databaseDirectory = System.getProperty("fizteh.db.dir");

        if (databaseDirectory == null) {
            System.err.println("You haven't set database directory");
            System.exit(1);
        }

        try {
            DatabaseTableProviderFactory factory = new DatabaseTableProviderFactory();
            StoreableShellState shellState = new StoreableShellState();
            shellState.provider = (DatabaseTableProvider) factory.create(databaseDirectory);
            shell.setShellState(shellState);
        } catch (IOException e) {
            System.err.println("some error occured during loading");
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("error while loading: " + e.getMessage());
            System.exit(1);
        }

        shell.start();
    }
}
