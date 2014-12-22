package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.CommandGetter;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.InteractiveGetter;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ExitCommandException;
import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.AdvancedTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.clientCommands.ClientCommand;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.serverCommands.ServerCommand;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands.TableCommand;

import java.util.NoSuchElementException;

/**
 * @author AlexeyZhuravlev
 */
public class TelnetMain {
    public static void main(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("No database directory name specified");
            System.exit(1);
        }
        try {
            CommandGetter getter = new InteractiveGetter();
            TableProviderFactory factory = new AdvancedTableProviderFactory();
            TableProvider dbDir = factory.create(path);
            ShellTableProvider serverProvider = new ShellTableProvider(dbDir);
            ServerLogic server = new ServerLogic(dbDir);
            ClientLogic client = new ClientLogic();
            ConnectionsCloser closer = new ConnectionsCloser(server, client);
            Runtime.getRuntime().addShutdownHook(closer);
            boolean exit = false;
            while (!exit) {
                String textCommand = getter.nextCommand();
                try {
                    ServerCommand command = ServerCommand.fromString(textCommand);
                    if (client.isConnected()) {
                        System.out.println("Client state now, unable to run server commands");
                        throw new UnknownCommandException();
                    }
                    command.execute(server);
                } catch (UnknownCommandException e) {
                    try {
                        ClientCommand command = ClientCommand.fromString(textCommand);
                        command.execute(client);
                    } catch (UnknownCommandException r) {
                        try {
                            TableCommand command = TableCommand.fromString(textCommand);
                            if (client.isConnected()) {
                                try {
                                    command.execute(client.getShellProvider(), System.out);
                                } catch (NoSuchElementException v) {
                                    System.out.println("server closed connection");
                                    client.disconnect();
                                }
                            } else {
                                command.execute(serverProvider, System.out);
                            }
                        } catch (UnknownCommandException t) {
                            System.err.println("Unknown command");
                        } catch (ExitCommandException t) {
                            exit = true;
                            closer.run();
                        } catch (Exception t) {
                            System.err.println(t.getMessage());
                        }
                    } catch (Exception w) {
                        System.err.println(w.getMessage());
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
