package ru.fizteh.fivt.students.AliakseiSemchankau.storeable;

import java.util.Map;
import java.util.Vector;

/**
 * Created by Aliaksei Semchankau on 17.10.2014.
 */
public class TableShowTables implements TableInterface {
    @Override
    public void makeCommand(Vector<String> args, DatabaseProvider dProvider) {

        for (Map.Entry<String, DatabaseTable> entry : dProvider.referenceToTableInfo.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().size());
        }
    }
}