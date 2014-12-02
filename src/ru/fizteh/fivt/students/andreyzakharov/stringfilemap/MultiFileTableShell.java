package ru.fizteh.fivt.students.andreyzakharov.stringfilemap;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.andreyzakharov.stringfilemap.commands.CommandRunner;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class MultiFileTableShell {
    private CommandRunner commandrunner = new CommandRunner();
    boolean batch;
    Path dbPath;
    TableProvider activeConnector;

    public MultiFileTableShell(boolean batch) {
        String property = System.getProperty("fizteh.db.dir");
        if (property == null || property.trim().isEmpty()) {
            System.err.println("Database root directory not set");
            System.exit(1);
        }
        dbPath = Paths.get(System.getProperty("user.dir")).resolve(property);
        this.batch = batch;

        MultiFileTableProviderFactory factory = new MultiFileTableProviderFactory();
        activeConnector = factory.create(property);
    }

    public static void main(String[] args) {
        MultiFileTableShell m = new MultiFileTableShell(args.length > 0);
        m.run(args);
    }

    void run(String[] args) {
        try (MultiFileTableProvider connector = new MultiFileTableProvider(dbPath)) {
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

    void execute(MultiFileTableProvider connector, String argString) {
        try {
            String out = commandrunner.run(connector, argString);
            if (out != null) {
                System.out.println(out);
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
