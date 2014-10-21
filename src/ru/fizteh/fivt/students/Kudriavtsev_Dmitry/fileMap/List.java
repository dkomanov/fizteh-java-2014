package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.fileMap;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class List extends Command{
    public List() {
        name = "list";
        argLen = 0;
    }

    @Override
    public boolean exec(HashMap<String, String> dBase, String[] args) {
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        Set<String> keySet = dBase.keySet();
        for (String key : keySet) {
            System.out.print(key + " , ");
        }
        System.out.println();
        return true;
    }
}
