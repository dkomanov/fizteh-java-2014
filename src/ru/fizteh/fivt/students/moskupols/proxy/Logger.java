package ru.fizteh.fivt.students.moskupols.proxy;

import java.io.IOException;

/**
 * Created by moskupols on 25.12.14.
 */
public interface Logger {
    void putMessage(String msg) throws IOException;
}
