package ru.fizteh.fivt.students.standy66_new.exceptions;

/**
 * Created by astepanov on 20.10.14.
 */
public class InterpreterInterruptionException extends Exception {
    public InterpreterInterruptionException() {
        super("got exit command");
    }
}
