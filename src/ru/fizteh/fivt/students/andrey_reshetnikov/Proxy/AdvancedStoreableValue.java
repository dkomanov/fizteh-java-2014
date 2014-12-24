package ru.fizteh.fivt.students.andrey_reshetnikov.Proxy;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.andrey_reshetnikov.Storeable.StoreableValue;

import java.util.List;
import java.util.Vector;

public class AdvancedStoreableValue extends StoreableValue {

    Storeable origin;

    protected AdvancedStoreableValue(Storeable passedOrigin) {
        origin = passedOrigin;
    }

    public Storeable getOrigin() {
        return origin;
    }

    @Override
    public String toString() {
        List<String> parsed = new Vector<>();
        for (Object value : ((StoreableValue) origin).getValues()) {
            if (value == null) {
                parsed.add("");
            } else {
                parsed.add(value.toString());
            }
        }
        return this.getClass().getSimpleName() + "[" + String.join(",", parsed) + "]";
    }
}
