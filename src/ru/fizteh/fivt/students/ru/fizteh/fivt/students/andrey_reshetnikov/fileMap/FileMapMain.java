
package ru.fizteh.fivt.students.ru.fizteh.fivt.students.andrey_reshetnikov.fileMap;

import java.io.IOException;

public class FileMapMain {
    private static boolean selectMode;

    public static void main(String[] args) throws IOException {
        new FileMapMain().run(args);
    }

    public void run(String[] args) throws IOException {
        selectMode = (args.length == 0);
        String path = System.getProperty("db.file");
        if (path == null) {
            System.err.println("Specify the database in properties");
            System.exit(1);
        }

        FileBase base = new FileBase();
        try {
            base.load(path);
        } catch (Exception e) {
            System.err.println("error");
            System.err.println(e.getMessage());
            System.exit(1);
        }

        CommandProcess commandProcess;
        if (selectMode) {
            commandProcess = new InteractiveMode();
        } else {
            commandProcess = new BatchMode(args);
        }

        try {
            commandProcess.process(new FileMapAction(base));
        } catch (UnknownCommand e) {
            System.err.println("Unknown command");
            System.exit(1);
        }
        base.dump(path);
    }
}
