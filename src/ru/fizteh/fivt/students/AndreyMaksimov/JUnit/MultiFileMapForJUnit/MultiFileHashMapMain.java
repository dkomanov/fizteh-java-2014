package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;


public class MultiFileHashMapMain {
    public static void main(String[] args) {
        standardRunner(args, new CommandExecutor());
    }

    public static void standardRunner(String[] args, AbstractCommandExecutor commandExecutor) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("No database directory name defined");
            System.exit(1);
        }
        boolean interactive = false;
        try {
            DataBaseDir dbDir = new DataBaseDir(path);
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
                    commandExecutor.executeNextCommand(getter, dbDir);
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


