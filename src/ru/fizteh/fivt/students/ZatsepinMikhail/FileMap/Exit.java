package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class Exit extends Command {
    public Exit() {
        name = "exit";
        numberOfArguments = 0;
    }
    public boolean run() {
        System.out.println("1");
        return true;
    }
}
