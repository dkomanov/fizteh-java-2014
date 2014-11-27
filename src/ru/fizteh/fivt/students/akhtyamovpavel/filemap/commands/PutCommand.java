package ru.fizteh.fivt.students.akhtyamovpavel.filemap.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.filemap.FileMap;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class PutCommand implements Command {
    private FileMap map;

    public PutCommand(FileMap link) {
        map = link;
    }
    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 2) {
            throw new Exception("usage: put key value");
        }
        if (map.containsKey(arguments.get(0))) {
            System.out.println("overwrite");
            System.out.println(map.get(arguments.get(0)));
        } else {
            System.out.println("new");
        }
        map.put(arguments.get(0), arguments.get(1));
        map.saveMap();
    }

    @Override
    public String getName() {
        return "put";
    }
}
