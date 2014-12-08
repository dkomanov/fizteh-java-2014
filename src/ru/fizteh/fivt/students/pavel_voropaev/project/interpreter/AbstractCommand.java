package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

public abstract class AbstractCommand implements Command {

    private final String name;
    private final int argNum;
    protected DatabaseInterpreterState state;

    protected AbstractCommand(String name, int argNum, DatabaseInterpreterState state) {
        this.name = name;
        this.argNum = argNum;
        this.state = state;
    }

    protected AbstractCommand(String name, int argNum, InterpreterState state) {
        this.name = name;
        this.argNum = argNum;
        this.state = (DatabaseInterpreterState) state;
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
