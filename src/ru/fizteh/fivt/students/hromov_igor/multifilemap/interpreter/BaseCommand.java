package ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter;


public abstract class BaseCommand {

    public abstract void run();

    public void putArguments(String[] args){
    }

    public abstract int requiredArgsNum();

}
