package ru.fizteh.fivt.students.akhtyamovpavel.filemap.commands;

import ru.fizteh.fivt.students.akhtyamovpavel.filemap.FileMap;

import java.util.ArrayList;

/**
 * Created by user1 on 06.10.2014.
 */
public class ListCommand implements Command{
    private FileMap fileMap;

    public ListCommand(FileMap link) {
        fileMap = link;
    }

    @Override
    public void executeCommand(ArrayList<String> arguments) throws Exception {
        if (!arguments.isEmpty()) {
            throw new Exception("usage: list");
        }
        String result = new String();

        System.out.println(String.join(", ", fileMap.keySet()));
    }

    @Override
    public String getName() {
        return "list";
    }
}
