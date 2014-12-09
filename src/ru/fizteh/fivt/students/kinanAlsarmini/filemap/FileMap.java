package ru.fizteh.fivt.students.kinanAlsarmini.filemap;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.Shell;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExternalCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.ExitCommand;
import java.io.IOException;
import java.io.File;

public class FileMap {
    private Table table;
    private File databaseFile;
    private Shell shell;

    public FileMap(String databaseDir) {
        if (databaseDir == null) {
            throw new IllegalArgumentException("Filemap dir is undefined");
        }

        databaseFile = new File(databaseDir);

        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                throw new IllegalArgumentException(databaseDir + " can't be created.");
            }
        }

        if (!databaseFile.isFile()) {
            throw new IllegalArgumentException(databaseDir + " isn't a proper file.");
        }

        table = new Table();
        readTable();

        ExternalCommand[] possibleCommands = new ExternalCommand[] {
                new ExitCommand(),
                new PutCommand(table),
                new GetCommand(table),
                new RemoveCommand(table),
                new ListCommand(table)
        };

        shell = new Shell(possibleCommands);
    }

    public Table getTable() {
        return new Table(table);
    }

    private void readTable() { 
        try {
            TableReader tableReader = new TableReader(databaseFile);
            tableReader.readTable(table);
            tableReader.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(databaseFile.getName() + " doesn't exist or isn't a proper file.");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage() + " Argument: " + databaseFile);
        }
    }

    public void clear() {
        table.clear();
    }

    public void close() {
        writeTable();
        shell.terminate();
    }

    private void writeTable() {
        try {
            TableWriter tableWriter = new TableWriter(databaseFile);
            tableWriter.writeTable(table);
            tableWriter.close();
        } catch (IOException e) {
            throw new IllegalArgumentException(databaseFile + " can't be opened for writing.");
        }
    }

    public void startInteractive() {
        shell.startInteractive();
    }

    public void startBatch(String commands) {
        shell.startBatch(commands);
    }
}
