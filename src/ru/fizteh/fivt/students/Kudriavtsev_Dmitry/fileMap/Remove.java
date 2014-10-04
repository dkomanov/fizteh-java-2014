package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.fileMap;

import java.util.HashMap;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Remove extends Command {
    public Remove() {
        name = "remove";
        argLen = 1;
    }

    @Override
    public boolean exec(HashMap<String, String> dBase, String[] args) {
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        if (dBase.remove(args[0])!= null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }

}
