package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class Remove extends Command {
    public Remove() {
        name = "remove";
        numberOfArguments = 1;
    }
    public boolean run() {
        System.out.println("1");
        return true;
    }
}
