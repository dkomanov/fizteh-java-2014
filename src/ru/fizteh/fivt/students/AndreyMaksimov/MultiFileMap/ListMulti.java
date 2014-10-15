package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.DataBase;
import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.List;

public class ListMulti extends Command {

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            List list = new List();

            String[] s = new String[1];
            s[0] = "list";

            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    DataBase cur = base.getUsing().databases[i][j];
                    if (cur != null) {
                        list.startNeedInstruction(s, cur);
                    }
                }
            }
        }
    }
}
