package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;


import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.BatchMode;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.CommandGetter;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.ExitException;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.InteractiveMode;

public class JUnitMain {
    public static void main(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("No database directory name specified");
            System.exit(1);
        }
        boolean interactive = false;
        try {
            JUnitDataBaseDir jUnitDbDir = new JUnitDataBaseDir(path);
            CommandGetter getter;
            if (args.length == 0) {
                interactive = true;
                getter = new InteractiveMode();
            } else {
                getter = new BatchMode(args);
            }
            boolean exitStatus = false;
            do {
                try {
                    String s = getter.nextCommand();
                    JUnitCommand command = JUnitCommand.fromString(s);
                    command.execute(jUnitDbDir);
                } catch (ExitException e) {
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