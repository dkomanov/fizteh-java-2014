package ru.fizteh.fivt.students.Volodin_Denis.MultiFileMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class MultiFileMap {
    
    private static HashMap<String, Integer> dbInformation; 
    
    public static void main(final String[] args) {    
        try {
            Table table = new Table();
            table.setPath(System.getProperty("fizteh.db.dir"));
                        
            String[] names = Paths.get(table.getPath()).toFile().list();
            try {
                dbInformation = new HashMap<String, Integer>();
                for (String name : names) {
                    if (Paths.get(table.getPath(), name).toFile().isFile()) {
                        Files.delete(Paths.get(table.getPath(), name));
                    }
                    if (Paths.get(table.getPath(), name).toFile().isDirectory()) {
                        DataBase temp = new DataBase(Paths.get(table.getPath(), name).normalize().toString());
                        dbInformation.put(name, temp.list().length);
                        temp.close();
                    }
                }
            } catch (Exception exception) {
                System.err.println(exception.getMessage());
                System.exit(ReturnCodes.ERROR);
            }
            
            table.setTable(null);
            if (args.length == 0) {
                InteractiveMode.interactive(table, dbInformation);
            } else {
                BatchMode.batch(args, table, dbInformation);
            }
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.exit(ReturnCodes.ERROR);
        }
        System.exit(ReturnCodes.SUCCESS);
    }
}
