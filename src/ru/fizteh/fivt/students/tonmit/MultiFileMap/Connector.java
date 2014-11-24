package ru.fizteh.fivt.students.tonmit.MultiFileMap;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;



public class Connector {
    protected Map<String, Command> commands = new HashMap<>();
    protected Path dbRootDirectory;
    protected Map<String, MFHMap> tables;
    protected MFHMap currentTable;
    
    private void printErrorAndExit(String errorStr) {
        System.err.println(errorStr);
        System.exit(-1);
    }

    public Connector(Path dbPath) {
        if (!Files.exists(dbPath)) {
            printErrorAndExit("destination does not exist");
        }
        if (!Files.isDirectory(dbPath)) {
            printErrorAndExit("destination is not a directory");
        }
        dbRootDirectory = dbPath;
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
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dbRootDirectory)) {
                for (Path file : stream) {
                    if (Files.isDirectory(file)) {
                        MFHMap table = new MFHMap(file);
                        table.load();
                        tables.put(file.getFileName().toString(), table);
                    }
                }
            } catch (IOException e) {
                printErrorAndExit("can't load the database");
            }
        }
    }

    public void close() {
        if (tables != null) {
            for (MFHMap table : tables.values()) {
                table.unload();
            }
        }
        if (currentTable != null) {
            currentTable.unload();
        }
    }

    public void run(String name, String[] args, boolean packageMode) {
        Command command;
        if (commands.containsKey(name)) {
            command = commands.get(name);
            command.packageMode = packageMode;
            command.exec(this, args);
        } else {
            System.err.println(args[0] + ": command not found");
        }
    }
}
