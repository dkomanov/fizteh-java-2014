package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public abstract class ParentCommand {

    public CommandState state;

    public ParentCommand(CommandState s) {
        this.state = s;
    }

    public abstract void run();

    public void putArguments(String[] args){
    }

    public abstract int requiredArgsNum();

}
