package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.util.HashMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class Remove extends Command {
    public Remove() {
        name = "remove";
        numberOfArguments = 2;
    }
    @Override
    public boolean run(HashMap<String, String> dataBase, String[] args) {
        if (args.length != numberOfArguments) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        String value = dataBase.remove(args[1]);
        if (value != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
        return true;
    }
}
