package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DbMain {
    Map<String, Command> commands = new HashMap<>();
    boolean batch;
    Path dbPath;
    TableProvider activeConnector;

    public DbMain(boolean batch) {
        String property = System.getProperty("fizteh.db.dir");
        if (property == null) {
            System.err.println("Database root directory not set");
            System.exit(1);
        }
        dbPath = Paths.get(System.getProperty("user.dir")).resolve(property);
        this.batch = batch;

        DbConnectorFactory factory = new DbConnectorFactory();
        activeConnector = factory.create(property);

        commands.put("create", new CreateCommand());
        commands.put("drop", new DropCommand());
        commands.put("use", new UseCommand());
        commands.put("show", new ShowCommand());

        commands.put("put", new PutCommand());
        commands.put("get", new GetCommand());
        commands.put("list", new ListCommand());
        commands.put("remove", new RemoveCommand());

        commands.put("exit", new ExitCommand());
    }

    public static void main(String[] args) {
        DbMain m = new DbMain(args.length > 0);
        m.run(args);
    }

    void run(String[] args) {
        try (DbConnector connector = new DbConnector(dbPath)) {
            if (batch) {
                StringBuilder sb = new StringBuilder();
                for (String s : args) {
                    sb.append(s.trim());
                    sb.append(' ');
                }
                String[] cmds = sb.toString().split(";");

                for (String cmd : cmds) {
                    execute(connector, cmd);
                }
            } else {
                Scanner scanner = new Scanner(System.in);
                scanner.useDelimiter("\\s*[;\\n]\\s*");

                System.out.print("# ");
                while (scanner.hasNext()) {
                    execute(connector, scanner.next().trim());
                    System.out.print("# ");
                }
                scanner.close();
            }
        } catch (ConnectionInterruptException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

    }

    void execute(DbConnector connector, String argString) {
        try {
            String[] args = argString.trim().split("\\s+");
            Command command = commands.get(args[0]);
            if (command != null) {
                String out = command.execute(connector, args);
                if (out != null) {
                    System.out.println(out);
                }
            } else if (!args[0].equals("")) {
                throw new CommandInterruptException(args[0] + ": command not found");
            }
        } catch (CommandInterruptException e) {
            System.err.println(e.getMessage());
            if (batch) {
                connector.close();
                System.exit(1);
            }
        }
    }
}
