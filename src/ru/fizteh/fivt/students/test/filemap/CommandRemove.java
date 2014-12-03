package ru.fizteh.fivt.students.deserg.filemap;

import java.util.ArrayList;

/**
 * Created by deserg on 03.10.14.
 */
public class CommandRemove implements Command {

    public void execute(ArrayList<String> args, FileMap fileMap) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            String key = args.get(1);
            String value = fileMap.get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
                fileMap.remove(key);
                fileMap.write();
            }

        } else {
            System.out.println("Too many arguments");
        }

    }

}
