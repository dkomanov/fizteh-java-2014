package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.nio.file.Path;

public class CommandExecutor {
        private Path dbdir;
        private Path olddir;
        private Table table;
        private MapCommands mapcommands;
        private FileManager db;
        CommandExecutor(Path dbdirectory) {
            dbdir = dbdirectory;
            olddir = dbdirectory;
            table = new Table(dbdir);
            FileManager db = new FileManager();
            mapcommands = new MapCommands(db);
        }
        void executeCommands(String[] command) {
        String key = new String();
        String value = new String();
        try {
            switch(command[0]) {
            case "put":
                if (!olddir.equals(dbdir)) {
                    key = command[1];
                    value = command[2];
                    mapcommands.put(key, value); 
                } else {
                    System.out.println("You should select table first");
                }
                break;
            case "get":
                if (!olddir.equals(dbdir)) {
                    key = command[1];
                    mapcommands.get(key);
                } else {
                    System.out.println("You should select table first");
                }
                break;
            case "remove":
                if (!olddir.equals(dbdir)) {
                    key = command[1];
                    mapcommands.remove(key);
                } else {
                    System.out.println("You should select table first");
                }
                break;
            case "list": 
                if (!olddir.equals(dbdir)) {
                    mapcommands.list();
                } else {
                    System.out.println("You should select table first");
                }
                break;
            case "exit":
                if (!olddir.equals(dbdir)) {
                    mapcommands.exit(db, olddir);
                }
                break;
            case "create":
                key = command[1];
                table.create(key);
                break;
            case "drop":
                key = command[1];
                table.drop(key);
                break;
            case "use":
                key = command[1];
                FileManager dbbuffer = new FileManager();
                dbbuffer = table.use(key, olddir, mapcommands.filemap);
                if (dbbuffer != null) {
                    db = dbbuffer;
                    mapcommands = new MapCommands(db);
                    olddir = dbdir.resolve(key);
                }
                break;
            case "showTables":
                table.showTables(olddir, mapcommands.filemap);          
                break;
            default:
                System.out.println("Invalid command");
                break;
            }
        } catch (Exception e) {
            System.out.println("Invalid number of arguments");
        }
    }
}     
