package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

public final class IllegalNumberOfArguments extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;
    
    IllegalNumberOfArguments(String name) {
        super(name + ": Incorrect number of arguments");
    }
}
