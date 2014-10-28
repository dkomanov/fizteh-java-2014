package ru.fizteh.fivt.students.sautin1.junit;

import ru.fizteh.fivt.students.sautin1.junit.filemap.*;
import ru.fizteh.fivt.students.sautin1.junit.multifilemap.*;
import ru.fizteh.fivt.students.sautin1.junit.shell.Command;
import ru.fizteh.fivt.students.sautin1.junit.shell.Shell;
import ru.fizteh.fivt.students.sautin1.junit.shell.UserInterruptException;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class.
 * Created by sautin1 on 10/12/14.
 */
public class Main {
    private static final String PROPERTY_NAME = "fizteh.db.dir";

    public static void main(String[] args) {
        System.setProperty(PROPERTY_NAME,
                "/home/sautin1/IdeaProjects/MIPTProjects/src/ru/fizteh/fivt/students/sautin1/test");
        int exitStatus = 0;
        try {
            Path filePath;
            String filePathString = System.getProperty(PROPERTY_NAME);
            if (filePathString == null) {
                throw new IllegalArgumentException("Property " + PROPERTY_NAME + " does not exist");
            } else {
                filePath = Paths.get(filePathString);
            }

            StringTableIOToolsMultipleFiles ioTools = new StringTableIOToolsMultipleFiles(16, 16, "UTF-8");
            StringTableProviderFactory providerFactory = new StringTableProviderFactory();
            StringTableProvider provider = providerFactory.create(filePath, false, ioTools);
            provider.loadAllTables();
            StringDatabaseState databaseState = new StringDatabaseState(provider);

            Command[] commands = {
                    new PutCommand(), new GetCommand(), new ListCommand(),
                    new RemoveCommand(), new CreateCommand(), new DropCommand(),
                    new ShowTablesCommand(), new UseCommand(), new CommitCommand(),
                    new RollbackCommand(), new SizeCommand(), new ExitCommand<>()
            };
            @SuppressWarnings("unchecked")
            Shell<StringDatabaseState> shell = new Shell<>(databaseState, commands);
            try {
                shell.startWork(args);
            } catch (UserInterruptException e) {
                exitStatus = 0;
                provider.saveAllTables();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            exitStatus = 1;
        }
        System.exit(exitStatus);
    }

}
