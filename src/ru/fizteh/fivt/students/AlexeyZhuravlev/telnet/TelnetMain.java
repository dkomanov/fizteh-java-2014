package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.CommandGetter;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.InteractiveGetter;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.ExitCommandException;
import ru.fizteh.fivt.students.AlexeyZhuravlev.proxy.AdvancedTableProviderFactory;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.serverCommands.ServerCommand;
import ru.fizteh.fivt.students.AlexeyZhuravlev.telnet.tableCommands.TableCommand;

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
            boolean exit = false;
            while (!exit) {
                String textCommand = getter.nextCommand();
                try {
                    ServerCommand command = ServerCommand.fromString(textCommand);
                    command.execute(server);
                } catch (UnknownCommandException e) {
                    try {
                        TableCommand command = TableCommand.fromString(textCommand);
                        command.execute(serverProvider, System.out);
                    } catch (UnknownCommandException t) {
                        System.err.println("Unknown command");
                    } catch (ExitCommandException t) {
                        exit = true;
                    } catch (Exception t) {
                        System.err.println(t.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
