package ru.fizteh.fivt.students.deserg.filemap;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class CommandPut implements Command {

    public void execute(ArrayList<String> args, FileMap fileMap) {

        if (args.size() < 3) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 3) {

            String key = args.get(1);
            String value = args.get(2);

            if (fileMap.containsKey(key) && fileMap.get(key).equals(value)) {
                    System.out.println("overwrite\n" + value);
            } else {
                System.out.println("new");
                fileMap.put(key, value);
                fileMap.write();
            }

        } else {
            System.out.println("Too many arguments");
        }

    }
}
