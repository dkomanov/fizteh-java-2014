package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class FmCommandList extends CommandFileMap {
    public FmCommandList() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        List<String> allKeys = myFileMap.list();
        int counter = 0;
        for (String oneKey : allKeys) {
            if (counter > 0) {
                System.out.print(", ");
            }
            System.out.print(oneKey);
            ++counter;
        }
        if (counter > 0) {
            System.out.println();
        }
        return true;
    }
}
