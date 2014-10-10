package ru.fizteh.fivt.students.semenenko_denis.FileMap;

import java.io.RandomAccessFile;

/**
 * Created by denny_000 on 08.10.2014.
 */
public interface SaveInMemoryInterface {
    void write(final RandomAccessFile whereTo);
    void read(final RandomAccessFile whereFrom);
}
