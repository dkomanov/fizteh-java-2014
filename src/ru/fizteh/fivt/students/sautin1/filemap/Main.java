package ru.fizteh.fivt.students.sautin1.filemap;

import ru.fizteh.fivt.students.sautin1.filemap.shell.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main class.
 * Created by sautin1 on 10/12/14.
 */
public class Main {
    private static final String PROPERTY_NAME = "db.file";

    public static void main(String[] args) {
        int exitStatus = 0;
        try {
            /*System.setProperty(PROPERTY_NAME,
                    "/home/sautin1/IdeaProjects/MIPTProjects/src/ru/fizteh/fivt/students/sautin1/test2");*/
            Path filePath;
            String filePathString = System.getProperty(PROPERTY_NAME);
            if (filePathString == null) {
                throw new IllegalArgumentException("Property " + PROPERTY_NAME + " does not exist");
            } else {
                filePath = Paths.get(filePathString);
            }
            StringTable table = new StringTable(filePath.getFileName().toString(), true);

            StringTableIOTools tableIOTools = new StringTableIOTools();
            tableIOTools.readTable(filePath, table);

            StringTableProvider provider = new StringTableProvider(filePath, true, tableIOTools);
            StringDatabaseState databaseState = new StringDatabaseState(provider);
            databaseState.setActiveTable(table);
            Command[] commands = {
                    new PutCommand(), new GetCommand(), new ListCommand(),
                    new RemoveCommand(), new ExitCommand<StringDatabaseState>()
            };
            @SuppressWarnings("unchecked")
            Shell<StringDatabaseState> shell = new Shell<>(databaseState, commands, new ShellCommandParser());
            try {
                shell.startWork(args);
            } catch (UserInterruptException e) {
                exitStatus = 0;
            }

            tableIOTools.writeTable(filePath, table);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            exitStatus = 1;
        }
        System.exit(exitStatus);
    }

}
