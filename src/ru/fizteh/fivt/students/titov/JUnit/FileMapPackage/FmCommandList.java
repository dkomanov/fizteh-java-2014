package ru.fizteh.fivt.students.titov.JUnit.FileMapPackage;

import java.util.List;

public class FmCommandList extends CommandFileMap {
    public FmCommandList() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(FileMap myFileMap, String[] args) {
        List<String> allKeys = myFileMap.list();
        int counter = 0;
        for (String oneKey : allKeys) {
            if (counter > 0) {
                System.err.print(", ");
            }
            System.err.print(oneKey);
            ++counter;
        }
        if (counter > 0) {
            System.err.println();
        }
        return true;
    }
}
