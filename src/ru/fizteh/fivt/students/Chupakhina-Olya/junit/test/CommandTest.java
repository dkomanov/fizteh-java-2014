package ru.fizteh.fivt.students.olga_chupakhina.junit.test;

import ru.fizteh.fivt.students.olga_chupakhina.junit.JUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class CommandTest {
    private final String newLine = System.getProperty("line.separator");
    private ByteArrayOutputStream outputStream;
    private PrintStream printStream;

    @Before
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testWithInvalidCommandAtTheInvalidCommand() throws Exception {
        JUnit.doCommand("command");
    }

    @Test
    public final void testCommandWithoutUsingCurrentTable() throws Exception {
        JUnit.doCommand("put 1 1");
        assertEquals("no table" + newLine, outputStream.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testWithWrongNumberOfArguments() throws Exception {
        JUnit.doCommand("exit 1");
    }

    @Test
    public final void testWithEmptyStringInput() throws Exception {
        JUnit.doCommand("");
        assertEquals("", outputStream.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testWithWrongNumberOfArgumentsInShowTables() throws Exception {
        JUnit.doCommand("show tables 1");
    }

    @Test(expected = Exception.class)
    public final void testInvalidCommandInShowTables() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        JUnit.doCommand("show table");
    }

    @Test(expected = Exception.class)
    public final void testInvalidCommandInShowTables2() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        JUnit.doCommand("show");
    }

    @After
    public void tearDown() throws IOException {
        outputStream.close();
        printStream.close();
    }
}