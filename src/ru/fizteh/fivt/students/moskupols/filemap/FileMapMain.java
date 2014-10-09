package ru.fizteh.fivt.students.moskupols.filemap;

import ru.fizteh.fivt.students.moskupols.cliutils.*;

import java.io.IOException;

/**
 * Created by moskupols on 26.09.14.
 */
public class FileMapMain {
    public static void main(String[] args) throws IOException {
        String dbPath = System.getProperty("db.file");
        if (dbPath == null) {
            System.err.println("Specify database file in property `db.file'.");
            System.exit(1);
        }

/*
        final DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(dbPath));
        outputStream.writeBytes(Integer.);
        outputStream.writeInt(5);
        outputStream.writeInt(20);
        outputStream.writeByte(20);
        outputStream.close();
        final DataInputStream inputStream = new DataInputStream(new FileInputStream(dbPath));
        System.out.println(inputStream.readInt());
        System.out.println(inputStream.readInt());
        System.out.println(inputStream.readInt());
        System.out.println(inputStream.readByte());
        System.exit(0);
*/

        DataBaseTable db = null;
        try {
            db = new DataBaseTable(dbPath);
        } catch (Exception e) {
            System.err.format("Error while loading database %s:\n", dbPath);
            System.err.println(e.getMessage());
            System.exit(1);
        }

        CommandProcessor commandProcessor;
        if (args.length == 0) {
            commandProcessor = new InteractiveCommandProcessor("$ ");
        } else {
            commandProcessor = new PackageCommandProcessor(args);
        }

        try {
            commandProcessor.process(new TableCommandFactory(db));
        } catch (CommandExecutionException | UnknownCommandException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            try {
                db.dump(dbPath);
            } catch (IOException e) {
                System.err.println("Could not save database:");
                System.err.println(e.getMessage());
            }
        }
    }
}
