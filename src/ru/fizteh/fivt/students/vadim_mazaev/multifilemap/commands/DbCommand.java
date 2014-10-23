package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public abstract class DbCommand implements Command {
    private TableManager manager;
    
    public DbCommand(TableManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("manager is null");
        }
        this.manager = manager;
    }

    @Override
    public final String getName() {
        return this.getClass().getSimpleName();
    }
    
    public final TableManager getManager() {
        return manager;
    }

    @Override
    public abstract boolean checkArgs(int argLen);

    @Override
    public void execute(String[] cmdWithArgs) throws Exception {
        if (!checkArgs(cmdWithArgs.length)) {
            throw new IllegalNumberOfArguments(getName());
        }
        run(cmdWithArgs);
    }
    
    public abstract void run(String[] cmdWithArgs) throws Exception;
}
