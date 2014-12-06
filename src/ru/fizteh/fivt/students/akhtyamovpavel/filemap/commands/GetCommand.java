package ru.fizteh.fivt.students.akhtyamovpavel.filemap.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.filemap.FileMap;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class GetCommand implements Command {
    private FileMap fileMap;

    public GetCommand(FileMap link) {
        fileMap = link;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (arguments.size() != 1) {
            throw new Exception("usage: get key");
        }

        if (fileMap.containsKey(arguments.get(0))) {
            System.out.println("found");
            System.out.println(fileMap.get(arguments.get(0)));
        } else {
            System.out.println("not found");
        }

    }

    @Override
    public String getName() {
        return "get";
    }
}
