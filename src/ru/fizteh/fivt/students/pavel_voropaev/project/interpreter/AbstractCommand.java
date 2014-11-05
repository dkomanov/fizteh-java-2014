package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

public abstract class AbstractCommand<Type> implements Command {

    private final String name;
    private final int argNum;
    protected final Type context;

    protected AbstractCommand() {
        this.name = null;
        this.argNum = 0;
        this.context = null;
    }

    protected AbstractCommand(String name, int argNum, Type context) {
        this.name = name;
        this.argNum = argNum;
        this.context = context;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getArgsNum() {
        return argNum;
    }
}
