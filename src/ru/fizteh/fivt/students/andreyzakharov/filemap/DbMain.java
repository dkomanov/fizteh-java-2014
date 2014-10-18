package ru.fizteh.fivt.students.andreyzakharov.filemap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class DbMain {
    boolean batch;
    Path dbPath;

    public DbMain(String[] args) {
        if (System.getProperty("db.file") == null) {
            System.err.println("Database root directory is not set.");
            System.exit(1);
        }
        dbPath = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("db.file"));
        batch = args.length > 0;
        run(args);
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
        } catch (ConnectionInterruptException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        DbMain m = new DbMain(args);
    }
}
