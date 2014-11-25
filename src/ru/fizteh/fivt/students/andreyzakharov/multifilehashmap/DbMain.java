package ru.fizteh.fivt.students.andreyzakharov.multifilehashmap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DbMain {
    boolean batch;
    Path dbPath;

    public DbMain(String[] args) {
        String property = System.getProperty("fizteh.db.dir");
        if (property == null || property.equals("")) {
            System.err.println("Database root directory not set");
            System.exit(1);
        }
        dbPath = Paths.get(System.getProperty("user.dir")).resolve(property);
        batch = args.length > 0;
        run(args);
    }

    public static void main(String[] args) {
        DbMain m = new DbMain(args);
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

    void execute(DbConnector connector, String s) {
        try {
            String out = connector.run(s);
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
