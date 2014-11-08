package ru.fizteh.fivt.students.ivan_ivanov.multifilehashmap;

import ru.fizteh.fivt.students.ivan_ivanov.shell.Command;
import ru.fizteh.fivt.students.ivan_ivanov.shell.Executor;

public class MultiFileHashMapExecutor extends Executor {

    public MultiFileHashMapExecutor() {

        list();
    }

    @Override
public final void list() {

        Command create = new CmdCreate();
        mapOfCmd.put(create.getName(), create);
        Command drop = new CmdDrop();
        mapOfCmd.put(drop.getName(), drop);
        Command use = new CmdUse();
        mapOfCmd.put(use.getName(), use);
        Command show = new CmdShowTables();
        mapOfCmd.put(show.getName(), show);
        Command exit = new MultiFileHashMapExit();
        mapOfCmd.put(exit.getName(), exit);
        Command get = new MultiFileHashMapGet();
        mapOfCmd.put(get.getName(), get);
        Command put = new MultiFileHashMapPut();
        mapOfCmd.put(put.getName(), put);
        Command remove = new MultiFileHashMapRemove();
        mapOfCmd.put(remove.getName(), remove);
        Command list = new MultiFileHashMapList();
        mapOfCmd.put(list.getName(), list);
    }
}
