package ru.fizteh.fivt.students.vadim_mazaev.Interpreter;

public final class StopLineInterpretationException extends RuntimeException {
    private static final long serialVersionUID = 2146673936560423416L;
    
    public StopLineInterpretationException(String message) {
        super(message);
    }
}
