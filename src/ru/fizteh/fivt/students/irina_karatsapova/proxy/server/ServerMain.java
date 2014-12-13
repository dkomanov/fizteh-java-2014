package ru.fizteh.fivt.students.irina_karatsapova.proxy.server;

import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands.ExitCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands.ListusersCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands.StartCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.commands.server_commands.StopCommand;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.proxy.server.database.table_provider_factory.MyTableProviderFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ServerMain {
    public static String mainDir = "fizteh.db.dir";

    public static void main(String[] args) {
        System.setProperty(mainDir, "D:/tmp/db8-telnet");

        TableProviderFactory tableProviderFactory = new MyTableProviderFactory();
        if (System.getProperty(ServerMain.mainDir) == null) {
            System.err.println("Path to the database is not set up. Use -D" + ServerMain.mainDir + "=...");
            System.exit(1);
        }
        TableProvider tableProvider = tableProviderFactory.create(System.getProperty(ServerMain.mainDir));

        BufferedReader in  = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out, true);

        Interpreter interpreter = new Interpreter(in, out);
        interpreter.addCommand(new StartCommand());
        interpreter.addCommand(new StopCommand());
        interpreter.addCommand(new ListusersCommand());
        interpreter.addCommand(new ExitCommand());

        InterpreterStateServer state = new InterpreterStateServer(tableProvider, out);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (state.isStarted()) {
                    try {
                        state.stop();
                        out.close();
                        in.close();
                    } catch (Exception e) {
                        System.err.println("Can't stop the server");
                    }
                }
            }
        });

        interpreter.interactiveMode(state);
    }
}
