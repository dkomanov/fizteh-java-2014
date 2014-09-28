package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.util.HashMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class Get extends Command {
    public Get() {
        name = "get";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(HashMap<String, String> dataBase, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        String value = dataBase.get(String[1]);
        if (value != null) {
            System.out.println("found%n" + value);
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
