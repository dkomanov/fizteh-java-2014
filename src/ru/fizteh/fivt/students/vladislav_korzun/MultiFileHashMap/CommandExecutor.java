package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.nio.file.Path;

public class CommandExecutor {
        private Path dbdir;
        private Path olddir;
        CommandExecutor(Path dbdirectory) {
            dbdir = dbdirectory;
            olddir = dbdirectory;
        }
        void executeCommands(String[] command) {
        Table table = new Table(dbdir);
        FileManager db = new FileManager();
        MapCommands mapcommands = new MapCommands(db);
        String key = new String();
        String value = new String();        
        switch(command[0]) {
        case "put":
            key = command[1];
            value = command[2];
            mapcommands.put(key, value);
            break;
        case "get":
            key = command[1];
            mapcommands.get(key);
            break;
        case "remove":
            key = command[1];
            mapcommands.remove(key);
            break;
        case "list": 
            mapcommands.list();
            break;
        case "exit":
            mapcommands.exit(db, olddir);
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
            db.filemap = table.use(key, olddir);
            mapcommands = new MapCommands(db);
            olddir = dbdir.resolve(key);
            break;
        case "showTables":
            table.showTables();
            break;
        default:
            System.err.println("Invalid command");
            break;
        }
    }
}     
