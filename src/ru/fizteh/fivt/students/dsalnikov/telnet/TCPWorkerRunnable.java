package ru.fizteh.fivt.students.dsalnikov.telnet;

import ru.fizteh.fivt.students.dsalnikov.filemap.commands.*;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.CreateCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.DropCommand;
import ru.fizteh.fivt.students.dsalnikov.multifilemap.commands.UseCommand;
import ru.fizteh.fivt.students.dsalnikov.shell.Shell;
import ru.fizteh.fivt.students.dsalnikov.shell.StorableParser;
import ru.fizteh.fivt.students.dsalnikov.shell.commands.Command;
import ru.fizteh.fivt.students.dsalnikov.storable.Database;
import ru.fizteh.fivt.students.dsalnikov.storable.StorableTableProviderFactory;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPWorkerRunnable implements Runnable {
    private Socket clientSocket;

    public TCPWorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintStream output = new PrintStream(clientSocket.getOutputStream());
        ) {
            String workingDirectory = System.getProperty("fizteh.db.dir");
            StorableTableProviderFactory factory = new StorableTableProviderFactory();
            Database state = new Database(new File(workingDirectory), factory.create(workingDirectory));
            Shell sh = new Shell(new StorableParser(), clientSocket.getInputStream(), output, output);
            List<Command> commands = new ArrayList<>();
            commands.add(new PutCommand(state));
            commands.add(new ListCommand(state));
            commands.add(new RemoveCommand(state));
            commands.add(new GetCommand(state));
            commands.add(new ExitCommand(state));
            commands.add(new CreateCommand(state));
            commands.add(new DropCommand(state));
            commands.add(new UseCommand(state));
            commands.add(new CommitCommand(state));
            commands.add(new RollbackCommand(state));
            commands.add(new SizeCommand(state));
            sh.setCommands(commands);
            sh.run(input.readLine());
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
