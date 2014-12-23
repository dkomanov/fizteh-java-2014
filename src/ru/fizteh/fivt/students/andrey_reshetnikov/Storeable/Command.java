package ru.fizteh.fivt.students.andrey_reshetnikov.Storeable;

public abstract class Command {
    public abstract void execute(MyStoreableTableProvider base) throws Exception;
    protected void putArguments(String[] args) throws Exception {}
    protected abstract int numberOfArguments();
}
