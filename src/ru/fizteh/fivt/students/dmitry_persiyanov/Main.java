package ru.fizteh.fivt.students.dmitry_persiyanov;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands_parser.DbCommandsParser;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.Interpreter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public final class Main {
    public static final String PROMPT = "$ ";
    private static File rootDir;
    private static DbManager dbManager;
    private static DbCommandsParser dbParser;

    public static void main(final String[] args) {
        String dbdir = System.getProperty("fizteh.db.dir");
        if (dbdir == null) {
            System.err.println("You must specify a variable \"fizteh.db.dir\".");
            System.exit(1);
        } else {
            rootDir = new File(dbdir);
            if (!rootDir.exists() || !rootDir.isDirectory()) {
                System.err.println("fizteh.db.dir isn't a directory");
                System.exit(1);
            }
        }
        try {
            dbManager = new DbManager(rootDir);
            dbParser = new DbCommandsParser(dbManager);
            Interpreter interpreter = new Interpreter(dbParser);
            if (args.length == 0) {
                interpreter.run();
            } else {
                InputStream in = buildStreamFromArgs(args);
                interpreter.run(in, Interpreter.ExecutionMode.BATCH);
            }
        } catch (RuntimeException e) {
            System.err.println("Cannot open database: " + e.getMessage());
        }
    }

    private static ByteArrayInputStream buildStreamFromArgs(final String[] args) {
        return new ByteArrayInputStream(String.join(" ", args).getBytes());
    }
}
