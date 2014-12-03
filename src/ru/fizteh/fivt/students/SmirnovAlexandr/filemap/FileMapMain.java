package ru.fizteh.fivt.students.SmirnovAlexandr.filemap;

/**
 * Created by San4eS on 12.10.14.
 */
import java.io.IOException;

public class FileMapMain {
    public static void main(String[] args) throws IOException {
        new FileMapMain().run(args);
    }

    public void run(String[] args) throws IOException {
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

        CommandProcess process;
        if (args.length == 0) {
            process = new InteractiveMode();
        } else {
            process = new PackageMode(args);
        }

        try {
            process.process(new FileMapAction(base));
        } catch (ExceptionUnknownCommand e) {
            System.err.println("Error: Unknown command");
            System.exit(1);
        }
        base.dump(path);
    }
}
