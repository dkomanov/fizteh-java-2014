package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap;

import java.util.List;

public class FmCommandList extends ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.CommandFileMap {
    public FmCommandList() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap myFileMap, String[] args) {
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
