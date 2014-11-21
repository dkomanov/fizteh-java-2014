package ru.fizteh.fivt.students.standy66_new.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.WrongNumberOfArgumentsException;

import java.util.function.IntPredicate;

/**
 * Created by astepanov on 20.10.14.
 */
public class Command {
    private IntPredicate isValidNumberOfArgumentsPredicate;

    protected Command(IntPredicate isValidNumberOfArgumentsPredicate) {
        this.isValidNumberOfArgumentsPredicate = isValidNumberOfArgumentsPredicate;
    }

    public void run(String[] arguments) throws Exception {
        if (!isValidNumberOfArgumentsPredicate.test(arguments.length)) {
            throw new WrongNumberOfArgumentsException("invalid number of arguments");
        }
    }
}
