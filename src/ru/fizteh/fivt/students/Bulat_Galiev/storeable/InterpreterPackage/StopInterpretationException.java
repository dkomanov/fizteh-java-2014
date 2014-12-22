package ru.fizteh.fivt.students.Bulat_Galiev.storeable.InterpreterPackage;

public class StopInterpretationException extends RuntimeException {
    private static final long serialVersionUID = -2962180363339747468L;

    public StopInterpretationException(final String message) {
        super(message);
    }
}
