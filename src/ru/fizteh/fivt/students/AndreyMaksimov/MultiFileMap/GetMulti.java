package ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap;

import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.DataBase;
import ru.fizteh.fivt.students.MaksimovAndrey.MultiFileMap.FileMapForMultiFileMap.Get;

public class GetMulti extends Command {
    private final String key;

    public GetMulti(String passedKey) {
        key = passedKey;
    }

    @Override
    public void startNeedMultiInstruction(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("ERROR: No such table");
        } else {
            int hashCode = key.hashCode();
            int dir = hashCode % 16;
            int file = hashCode / 16 % 16;

            String[] s = new String[2];
            s[0] = "get";
            s[1] = key;

            Get get = new Get();
            DataBase database = base.getUsing().databases[dir][file];
            if (database == null) {
                System.out.println("ERROR: No such DataBase");
            } else {
                get.startNeedInstruction(s, base.getUsing().databases[dir][file]);
            }
        }
    }
}

