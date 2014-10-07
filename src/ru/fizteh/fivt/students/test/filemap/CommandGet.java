package ru.fizteh.fivt.students.deserg.filemap;

import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public class CommandGet implements Command{

    public void execute(Vector<String> args, FileMap fileMap) {

        if (args.size() < 2) {
            throw new MyException("Not enough arguments");
        }
        if (args.size() == 2) {

            String key = args.get(1);
            String value = fileMap.get(key);
            if (value != null) {
                System.out.println("found\n" + value);
            } else {
                System.out.println("not found");
            }

        } else {
            System.out.println("Too many arguments");
        }

    }

}
