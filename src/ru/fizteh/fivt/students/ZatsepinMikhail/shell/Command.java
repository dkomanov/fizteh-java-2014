package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

/**
 * Created by mikhail on 19.09.14.
 */
abstract public class Command {
    String name;
    int numberOfArguments;
    abstract public boolean run(String[] arguments);
    public String getName(){
        return name;
    }
}
