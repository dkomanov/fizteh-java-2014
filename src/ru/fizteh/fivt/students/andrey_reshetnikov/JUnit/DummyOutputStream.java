package ru.fizteh.fivt.students.andrey_reshetnikov.JUnit;

import java.io.IOException;
import java.io.OutputStream;

public class DummyOutputStream extends OutputStream {

    @Override
    public void write(int b) throws IOException {}
}
