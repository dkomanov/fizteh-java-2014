package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class MultiFileHashMapMain {
    private static boolean selectMode;

    public static void main(String[] args) {
        new MultiFileHashMapMain().run(args);
    }

    public static void run(String[] args) {
        String path = System.getProperty("fizteh.db.dir");
        if (path == null) {
            System.err.println("Specify the database in properties");
            System.exit(1);
        }

        selectMode = (args.length == 0);
        try {
            DataBaseOneDir dataBaseDir = new DataBaseOneDir(path);
            CommandGetter getNewCommand;
            if (selectMode) {
                getNewCommand = new InteractiveGetter();
            } else {
                getNewCommand = new BatchGetter(args);
            }
            boolean exitStatus = false;
            do {
                try {
                    String s = getNewCommand.nextCommand();
                    Command command = Command.fromString(s);
                    command.execute(dataBaseDir);
                } catch (ExitCommandException e) {
                    exitStatus = true;
                } catch (Exception e) {
                    if (selectMode) {
                        System.err.println(e.getMessage());
                        System.err.flush();
                    } else {
                        throw e;
                    }
                }
            } while (!exitStatus);
        } catch (MkdirException e) {
            System.err.println("You cann't create a directory for the new table");
            System.exit(1);
        } catch (ExistsException e) {
            System.err.println("Database directory does not exists");
            System.exit(1);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
