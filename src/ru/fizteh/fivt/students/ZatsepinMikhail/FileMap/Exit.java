package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.util.HashMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class Exit extends Command {
    public Exit() {
        name = "exit";
        numberOfArguments = 0;
    }

    @Override
    public boolean run(HashMap<String, String> dataBase, String[] args) {
        System.out.println("exit");
        return true;
    }
}
