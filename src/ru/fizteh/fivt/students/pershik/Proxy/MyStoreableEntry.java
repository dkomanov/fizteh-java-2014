package ru.fizteh.fivt.students.pershik.Proxy;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.pershik.Storeable.StoreableEntry;

import java.util.List;

/**
 * Created by pershik on 11/28/14.
 */
public class MyStoreableEntry extends StoreableEntry implements Storeable {
    public MyStoreableEntry(List<Class<?>> newSignature) {
        super(newSignature);
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("");
        res.append(this.getClass().getSimpleName());
        res.append("[");
        for (Object object : this.list) {
            if (object == null) {
                res.append(",");
            } else {
                res.append(object.toString());
                res.append(",");
            }
        }
        if (res.lastIndexOf(",") == res.length() - 1) {
            res.deleteCharAt(res.length() - 1);
        }
        res.append("]");
        return res.toString();
    }
}
