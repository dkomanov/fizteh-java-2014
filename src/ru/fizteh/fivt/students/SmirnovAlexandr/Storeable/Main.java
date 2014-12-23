package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable;

import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.commands_parser.DbCommandsParser;
import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.interpreter.Interpreter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Main {
    private static Path rootDir;
    private static DbTableProvider tableProvider;
    private static DbCommandsParser dbParser;

    public static void main(final String[] args) {
        String dbdir = System.getProperty("fizteh.db.dir");
        if (dbdir == null) {
            System.err.println("You must specify a variable \"fizteh.db.dir\".");
            System.exit(1);
        } else {
            rootDir = Paths.get(dbdir);
        }
        try {
            tableProvider = new DbTableProvider(rootDir);
            dbParser = new DbCommandsParser(tableProvider);
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
