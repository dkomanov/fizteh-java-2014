package ru.fizteh.fivt.students.sautin1.multifilemap;

import ru.fizteh.fivt.students.sautin1.multifilemap.shell.*;
import ru.fizteh.fivt.students.sautin1.multifilemap.filemap.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class.
 * Created by sautin1 on 10/12/14.
 */
public class Main {
    private static final String PROPERTY_NAME = "fizteh.db.dir";

    public static void main(String[] args) {
        int exitStatus = 0;
        try {
            Path filePath;
            /*System.setProperty(PROPERTY_NAME,
                    "/home/sautin1/IdeaProjects/MIPTProjects/src/ru/fizteh/fivt/students/sautin1/test");*/
            String filePathString = System.getProperty(PROPERTY_NAME);
            if (filePathString == null) {
                throw new IllegalArgumentException("Property " + PROPERTY_NAME + " does not exist");
            } else {
                filePath = Paths.get(filePathString);
            }

            StringTableIOToolsMultipleFiles ioTools = new StringTableIOToolsMultipleFiles(16, 16, "UTF-8");
            StringTableProvider provider = new StringTableProvider(filePath, true, ioTools);
            provider.loadAllTables();
            StringDatabaseState databaseState = new StringDatabaseState(provider);

            Command[] commands = {
                    new PutCommand(), new GetCommand(), new ListCommand(),
                    new RemoveCommand(), new CreateCommand(), new DropCommand(),
                    new ShowTablesCommand(), new UseCommand(), new ExitCommand<>()
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
