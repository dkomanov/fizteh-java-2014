package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.fileMap;

import java.util.HashMap;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Put extends Command {

    public Put() {
        name = "put";
        argLen = 2;
    }

    @Override
    public boolean exec(HashMap<String, String> dBase, String[] args) {
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        String value = dBase.put(args[0], args[1]);
        if (value != null) {
            System.out.println("Overwrite:\n" + value);
        } else {
            System.out.println("new");
        }
        return true;
    }
}
