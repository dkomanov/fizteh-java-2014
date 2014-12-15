package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.CommandGetter;
import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.ExceptionExitCommand;
import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.InteractiveGetter;
import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.BatchGetter;

public class Handler {
    public Handler(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("No database directory name specified");
            System.exit(1);
        }
        boolean interactive = false;
        try {
            TableProvider jUnitDbDir = new MyTableProvider(path);
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
                    JCommand command = JCommand.fromString(s);
                    command.execute((MyTableProvider) jUnitDbDir);
                } catch (ExceptionExitCommand e) {
                    exitStatus = true;
                } catch (Exception e) {
                    if (interactive) {
                        System.err.println(e.getMessage());
                        System.err.flush();
                    } else {
                        throw e;
                    }
                }
            } while (!exitStatus);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
