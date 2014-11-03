package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.nio.file.Path;

public class CommandExecutor {
        private Path dbdir;
        private Path oldDirectory;
        private Table table;
        private MapCommands mapcommands;
        private FileManager db;
        
        private Boolean verify(String[] command, int desiredLength) throws Exception {
            if (!oldDirectory.equals(dbdir)) {
                if (command.length != desiredLength) {
                    throw new Exception("Invalid number of arguments");
                    
                }
                return true;
            } else {
                throw new Exception("no table"); 
            }
        }
        CommandExecutor(Path dbdirectory) {
            dbdir = dbdirectory;
            oldDirectory = dbdirectory;
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
                if (verify(command, 3)) {
                    mapcommands.put(key, value); 
                }
                
                break;
            case "get":
                if (verify(command, 2)) {
                    mapcommands.get(key); 
                }
                break;
            case "remove":
                if (verify(command, 2)) {
                    mapcommands.remove(key); 
                }
                break;
            case "list": 
                if (verify(command, 1)) {
                    mapcommands.list();
                }
                break;
            case "exit":
                if (command.length != 1) {
                    throw new Exception("Invalid number of arguments");
                }
                if (!oldDirectory.equals(dbdir)) {
                    mapcommands.exit(db, oldDirectory);
                }
                break;
            case "create":
                if (command.length != 2) {
                    throw new Exception("Invalid number of arguments");
                }
                key = command[1];
                table.create(key);
                break;
            case "drop":
                if (command.length != 2) {
                    throw new Exception("Invalid number of arguments");
                }
                key = command[1];
                table.drop(key, oldDirectory, mapcommands.filemap);
                break;
            case "use":
                if (command.length != 2) {
                    throw new Exception("Invalid number of arguments");
                }
                key = command[1];
                FileManager dbbuffer = new FileManager();
                dbbuffer = table.use(key, oldDirectory, mapcommands.filemap);
                if (dbbuffer != null) {
                    db = dbbuffer;
                    mapcommands = new MapCommands(db);
                    oldDirectory = dbdir.resolve(key);
                }
                break;
            case "show":
                if (command.length != 2) {
                    throw new Exception("Invalid number of arguments");
                }
                switch(command[1]) {
                case "tables": 
                    table.showTables(oldDirectory, mapcommands.filemap);
                    break;
                default:
                    System.out.println("Invalid command");
                    break;
                }
                break;
            default:
                System.out.println("Invalid command");
                break;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}     
