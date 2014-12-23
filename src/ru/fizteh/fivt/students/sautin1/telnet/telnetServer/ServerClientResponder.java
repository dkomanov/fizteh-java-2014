package ru.fizteh.fivt.students.sautin1.telnet.telnetServer;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.GetCommand;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.ListCommand;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.PutCommand;
import ru.fizteh.fivt.students.sautin1.telnet.filemap.RemoveCommand;
import ru.fizteh.fivt.students.sautin1.telnet.junit.CommitCommand;
import ru.fizteh.fivt.students.sautin1.telnet.junit.RollbackCommand;
import ru.fizteh.fivt.students.sautin1.telnet.junit.SizeCommand;
import ru.fizteh.fivt.students.sautin1.telnet.multifilemap.*;
import ru.fizteh.fivt.students.sautin1.telnet.shell.Command;
import ru.fizteh.fivt.students.sautin1.telnet.shell.Shell;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableDatabaseState;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableTableIOToolsMultipleFiles;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableTableProvider;
import ru.fizteh.fivt.students.sautin1.telnet.storeable.StoreableTableProviderFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

/**
 * Created by sautin1 on 12/16/14.
 */
public class ServerClientResponder implements Runnable {
    public static final String DB_LOCATION_PROPERTY = "fizteh.db.dir";
    private ThreadLocal<Socket> localSocket;
    private Shell<StoreableDatabaseState> shell;

    public ServerClientResponder(Socket socket) throws IOException, ParseException {
        localSocket = new ThreadLocal<Socket>() {
            @Override protected Socket initialValue() {
                return socket;
            }
        };

//        System.setProperty(DB_LOCATION_PROPERTY,
//        "/home/sautin1/IdeaProjects/MIPTProjects/src/ru/fizteh/fivt/students/sautin1/test");
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

        Command[] commands = {
                new PutCommand(), new GetCommand(), new ListCommand(),
                new RemoveCommand(), new CreateCommand(), new DropCommand(),
                new ShowTablesCommand(), new UseCommand(), new CommitCommand(),
                new RollbackCommand(), new SizeCommand(), new ExitCommand<>()
        };

        BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter outStream = new PrintWriter(socket.getOutputStream(), true);
        StoreableDatabaseState databaseState = new StoreableDatabaseState(provider, inStream, outStream);
        shell = new Shell<>(databaseState, commands);
    }

    @Override
    public void run() {
        Socket socket = localSocket.get();
        try {
            shell.startWork(new String[0]);
        } catch (Exception e) {
            // suppress
        }
    }
}
