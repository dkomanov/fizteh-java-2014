package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CommandFileMap {
    public abstract void execute(DataBaseOneFile base, AtomicBoolean exitStatus) throws Exception;
}
