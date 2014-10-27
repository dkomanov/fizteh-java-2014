package ru.fizteh.fivt.students.deserg.filemap;

import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public class CommandList implements Command {

    public void execute(Vector<String> args, FileMap fileMap) {

        if (args.size() == 1) {

            String out = "";
            for (String key: fileMap.keySet()) {
                out = out + key + ", ";
            }

            if (out.isEmpty()) {
                System.out.println("");
            } else {
                System.out.println(out.substring(0, out.length() - 2));
            }


        } else {
            System.out.println("Too many arguments");
        }

    }

}
