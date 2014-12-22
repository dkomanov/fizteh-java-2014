package ru.fizteh.fivt.students.ilivanov.Proxy;

import ru.fizteh.fivt.students.ilivanov.Proxy.Interfaces.Storeable;

import java.util.HashMap;

public class Transaction {
    HashMap<String, Storeable> diff;
    MultiFileMap table;

    public Transaction(MultiFileMap table) {
        this.table = table;
        diff = new HashMap<>();
    }

    public HashMap<String, Storeable> getDiff() {
        return diff;
    }

    public MultiFileMap getTable() {
        return table;
    }
}
