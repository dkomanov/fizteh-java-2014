package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.BaseCommand;

public abstract class ParentCommand extends BaseCommand {

    public CommandState state;

    public ParentCommand(CommandState s) {
        this.state = s;
    }

    public abstract void run();

    public void putArguments(String[] args){
    }

    public abstract int requiredArgsNum();

}
