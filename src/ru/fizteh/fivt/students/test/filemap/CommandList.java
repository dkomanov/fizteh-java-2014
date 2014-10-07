package ru.fizteh.fivt.students.deserg.filemap;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * Created by deserg on 03.10.14.
 */
public class CommandList implements Command {

    public void execute(Vector<String> args, FileMap fileMap) {

        if (args.size() == 1) {

            if (fileMap.size() > 0) {

                Iterator<String> it = fileMap.keySet().iterator();
                System.out.print(it.next());
                while (it.hasNext()) {
                    System.out.print(", " + it.next());
                }

            }

            System.out.println("");


        } else {
            System.out.println("Too many arguments");
        }

    }

}
