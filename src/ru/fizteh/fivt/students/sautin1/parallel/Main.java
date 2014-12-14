package ru.fizteh.fivt.students.sautin1.parallel;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.GetCommand;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.ListCommand;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.PutCommand;
import ru.fizteh.fivt.students.sautin1.parallel.filemap.RemoveCommand;
import ru.fizteh.fivt.students.sautin1.parallel.junit.CommitCommand;
import ru.fizteh.fivt.students.sautin1.parallel.junit.RollbackCommand;
import ru.fizteh.fivt.students.sautin1.parallel.junit.SizeCommand;
import ru.fizteh.fivt.students.sautin1.parallel.multifilemap.*;
import ru.fizteh.fivt.students.sautin1.parallel.shell.Command;
import ru.fizteh.fivt.students.sautin1.parallel.shell.Shell;
import ru.fizteh.fivt.students.sautin1.parallel.shell.UserInterruptException;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTableIOToolsMultipleFiles;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.sautin1.parallel.storeable.StoreableTableProviderFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

/**
 * Main class.
 * Created by sautin1 on 10/12/14.
 */
public class Main {
    public static final String DB_LOCATION_PROPERTY = "fizteh.db.dir";

    public static void main(String[] args) {
        int exitStatus = 0;
        try {
            //System.setProperty(DB_LOCATION_PROPERTY,
            // "/home/sautin1/IdeaProjects/MIPTProjects/src/ru/fizteh/fivt/students/sautin1/test");
            String filePathString = System.getProperty(DB_LOCATION_PROPERTY);
            if (filePathString == null) {
                throw new IllegalArgumentException("No system property \'" + DB_LOCATION_PROPERTY + "\' found");
            }
            Path filePath = Paths.get(filePathString);

            StoreableTableIOToolsMultipleFiles ioTools = new StoreableTableIOToolsMultipleFiles(16, 16, "UTF-8");
            StoreableTableProviderFactory providerFactory = new StoreableTableProviderFactory();
            StoreableTableProvider provider = providerFactory.create(filePath, false, ioTools);
            try {
                provider.loadAllTables();
            } catch (ParseException | ColumnFormatException e) {
                throw new ParseException("Cannot load tables: " + e.getMessage(), 0);
            }
            StoreableDatabaseState databaseState = new StoreableDatabaseState(provider);

            Command[] commands = {
                    new PutCommand(), new GetCommand(), new ListCommand(),
                    new RemoveCommand(), new CreateCommand(), new DropCommand(),
                    new ShowTablesCommand(), new UseCommand(), new CommitCommand(),
                    new RollbackCommand(), new SizeCommand(), new ExitCommand<>()
            };
            @SuppressWarnings("unchecked")
            Shell<StoreableDatabaseState> shell = new Shell<>(databaseState, commands);
            try {
                shell.startWork(args);
            } catch (UserInterruptException e) {
                exitStatus = 0;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            exitStatus = 1;
        }
        System.exit(exitStatus);
    }

}
