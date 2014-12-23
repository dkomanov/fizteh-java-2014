package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import java.io.IOException;
import java.io.OutputStream;

public class NoWriteOutputStream extends OutputStream {
    @Override
    public void write(int number) throws IOException {}
}
