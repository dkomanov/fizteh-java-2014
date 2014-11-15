package ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit.DataBase;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.FileMapForMultiFileMapForJUnit.List;

public class ListMulti extends Command {

    protected int numberOfArguments() {
        return 0;
    }

    @Override
    public void executeOnTable(Table table) throws Exception {
        StringBuilder allKeys = new StringBuilder();
        List list = new List();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                DataBase cur = table.databases[i][j];
                if (cur != null) {
                    String newList = list.getList(cur);
                    if (newList.length() > 0) {
                        if (allKeys.length() > 0) {
                            allKeys.append(", ");
                        }
                        allKeys.append(newList);
                    }
                }
            }
        }
        System.out.println(allKeys.toString());
    }

    @Override
    public void startNeedInstruction(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            executeOnTable(base.getUsing());
        }
    }
}


