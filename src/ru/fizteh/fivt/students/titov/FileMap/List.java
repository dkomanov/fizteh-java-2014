package ru.fizteh.fivt.students.titov.FileMap;

import java.util.Iterator;
import java.util.Set;

public class List extends CommandFileMap {
    public List() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        Set<String> keySet = myFileMap.keySet();
        Iterator<String> iteratorOverKeySet = keySet.iterator();
        int counter = 0;
        while (iteratorOverKeySet.hasNext()) {
            if (counter > 0) {
                System.out.print(", ");
            }
            System.out.print(iteratorOverKeySet.next());
            ++counter;
        }
        if (counter > 0) {
            System.out.println();
        }
        return true;
    }
}
