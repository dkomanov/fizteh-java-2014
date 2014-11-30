package ru.fizteh.fivt.students.standy66.shell;

/**
 * Abstract Action class.
 * Created by astepanov on 20.09.14.
 */
public abstract class Action {
    /**
     *
     */
    protected String[] arguments;

    /**
     * @param args
     */
    public Action(String[] args) {
        arguments = args;
    }

    /**
     * Run the action.
     */
    public abstract boolean run();
}
