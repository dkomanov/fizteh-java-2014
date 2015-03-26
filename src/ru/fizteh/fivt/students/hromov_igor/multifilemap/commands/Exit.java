package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

public class Exit extends ParentCommand {

    public Exit(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        throw new IllegalMonitorStateException();
    }

    @Override
    public int requiredArgsNum() {
        return 0;
    }
}
