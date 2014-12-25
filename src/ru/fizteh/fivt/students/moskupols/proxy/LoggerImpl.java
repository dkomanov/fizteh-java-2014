package ru.fizteh.fivt.students.moskupols.proxy;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by moskupols on 25.12.14.
 */
public class LoggerImpl implements Logger {
    private Writer writer;

    public LoggerImpl(Writer writer) {
        this.writer = writer;
    }

    @Override
    public synchronized void putMessage(String msg) throws IOException {
        writer.write(msg);
        writer.write("\n");
    }
}
