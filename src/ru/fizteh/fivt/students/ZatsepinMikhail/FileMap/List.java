package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

/**
 * Created by mikhail on 26.09.14.
 */
public class List extends Command {
    public List() {
        name = "list";
        numberOfArguments = 0;
    }
    public boolean run() {
        System.out.println("1");
        return true;
    }
}
