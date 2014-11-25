package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.parser.CommandsParser;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public final class DbInterpreter {
    public static final String PROMPT = "$ ";
    private static File rootDir;
    private static DbManager dbManager;

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
            if (args.length == 0) {
                interactiveMode();
            } else {
                batchMode(CommandsParser.parse(args));
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void interactiveMode() throws IOException {
        try (Scanner in = new Scanner(System.in)) {
            System.out.print(PROMPT);
            while (in.hasNextLine()) {
                try {
                    DbCommand cmd = CommandsParser.parse(in.nextLine());
                    dbManager.executeCommand(cmd);
                    if (!cmd.getMsg().equals("")) {
                        System.out.println(cmd.getMsg());
                    }
                } catch (IllegalArgumentException | TableIsNotChosenException e) {
                    System.err.println(e.getMessage());
                }
                System.out.print(PROMPT);
            }
        }
    }

    public static void batchMode(final DbCommand[] commands) throws IOException {
        for (DbCommand cmd : commands) {
            try {
                dbManager.executeCommand(cmd);
                System.out.println(cmd.getMsg());
            } catch (IllegalArgumentException | TableIsNotChosenException e) {
                System.err.println("error: " + e.getMessage());
                System.exit(1);
            }
        }
        dbManager.dumpCurrentTable();
        System.exit(0);
    }
}
