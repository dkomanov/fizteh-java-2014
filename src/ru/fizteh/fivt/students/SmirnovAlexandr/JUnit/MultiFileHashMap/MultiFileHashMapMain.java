package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap;

import java.io.UnsupportedEncodingException;

public class MultiFileHashMapMain {
    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("No database directory name specified");
            System.exit(1);
        }
        boolean interactive = false;
        try {
            DataBaseDir dbDir = new DataBaseDir(path);
            CommandGetter getter;
            if (args.length == 0) {
                interactive = true;
                getter = new InteractiveGetter();
            } else {
                getter = new BatchGetter(args);
            }
            boolean exitStatus = false;
            do {
                try {
                    String s = getter.nextCommand();
                    Command command = Command.fromString(s);
                    command.execute(dbDir);
                } catch (ExceptionExitCommand e) {
                    exitStatus = true;
                } catch (ExceptionEmptyCommand e) {
                    // do nothing
                } catch (Exception e) {
                    if (interactive) {
                        System.err.println(e.getMessage());
                        System.err.flush();
                    } else {
                        throw e;
                    }
                }
            } while (!exitStatus);
        } catch (UnsupportedEncodingException e) {
            System.out.println("Error: UTF-8 encoding is not supported");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
