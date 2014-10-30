package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Дмитрий on 08.10.14.
 */
public class Connector {
    protected Map<String, Command> commands = new HashMap<>();
    protected Path dbRoot;
    protected Map<String, MFHMap> tables;
    protected MFHMap activeTable;

    public Connector(Path dbPath) {
        if (!Files.exists(dbPath)) {
            System.err.println("destination does not exist");
            System.exit(-1);
        }
        if (!Files.isDirectory(dbPath)) {
            System.err.println("destination is not a directory");
            System.exit(-1);
        }
        dbRoot = dbPath;
        open();

        commands.put("create", new Create());
        commands.put("drop", new Drop());
        commands.put("use", new Use());
        commands.put("show", new Show());

        commands.put("put", new Put());
        commands.put("get", new Get());
        commands.put("list", new List());
        commands.put("remove", new Remove());
    }

    public void open() {
        if (tables == null) {
            tables = new HashMap<>();
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRoot)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        MFHMap table = new MFHMap(file);
                        table.load();
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                System.err.println("can't load the database");
                System.exit(-1);
            }
        }
    }

    public void close() {
        if (tables != null) {
            for (MFHMap table : tables.values()) {
                table.unload();
            }
        }
        if (activeTable != null) {
            activeTable.unload();
        }
    }

    public boolean run(String name, String[] args, boolean batchMode, boolean batchModeInInteractive) {
        Command command = commands.get(name);
        command.batchMode = batchMode;
        command.batchModeInInteractive = batchModeInInteractive;
        if (command != null) {
            if (!command.exec(this, args)) {
                return false;
            }
        } else if (!args[0].equals("")) {
            System.err.println("args[0] + : command not found");
            return false;
        }
        return true;
    }
}
