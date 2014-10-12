package ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap;

import ru.fizteh.fivt.students.AlexeyZhuravlev.filemap.DataBase;
import ru.fizteh.fivt.students.AlexeyZhuravlev.filemap.ListCommand;

/**
 * @author AlexeyZhuravlev
 */
public class MultiListCommand extends Command {
    @Override
    public void execute(DataBaseDir base) throws Exception {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            StringBuilder allKeys = new StringBuilder();
            ListCommand list = new ListCommand();
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    DataBase cur = base.getUsing().databases[i][j];
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
    }
}
