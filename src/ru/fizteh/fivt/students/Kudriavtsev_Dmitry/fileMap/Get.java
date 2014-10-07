package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.fileMap;

import java.util.HashMap;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Get extends Command{

    public Get() {
        name = "get";
        argLen = 1;
    }

    @Override
    public boolean exec(HashMap<String, String> dBase, String[] args) {
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        String value = dBase.get(args[0]);
        if (value != null) {
            System.out.println("found:\n" + value);
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
