package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class List extends Command {
    public List() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(HashMap<String, String> dataBase, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        Set<String> keySet = dataBase.keySet();
        Iterator<String> iteratorOverKeySet = keySet.iterator();
        int counter = 0;
        while (iteratorOverKeySet.hasNext()) {
            if (counter > 0) {
                System.out.print(", ");
            }
            System.out.print(iteratorOverKeySet.next());
            ++counter;
        }
        System.out.println();
        return true;
    }
}
