package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.util.HashMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class Put extends Command {
    public Put() {
        name = "put";
        numberOfArguments = 3;
    }
    @Override
    public boolean run(HashMap<String, String> dataBase, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        String oldValue = dataBase.put(args[1], args[2]);
        if (oldValue != null) {
            System.out.println("overwrite\n" + oldValue);
        } else {
            System.out.println("new");
        }
        return true;
    }
}
