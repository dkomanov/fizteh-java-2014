package ru.fizteh.fivt.students.dnovikov.junit.Interpreter;

import org.junit.Before;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class InterpreterTest {


    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }



}