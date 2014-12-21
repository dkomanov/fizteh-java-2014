package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.Assert.*;

public class InterpreterTest {

    private final char commandSeparator = ';';
    private final String testCommand = "test";
    @Rule
    public TemporaryFolder tmpFolder = new TemporaryFolder();
    public StoreableTableProvider provider;
    public Welcome state;
    private ByteArrayOutputStream outputStream;
    private ByteArrayOutputStream outputStreamError;
    private PrintStream printStream;
    private PrintStream printStreamError;

    @Before
    public void setUp() throws IOException {
        provider = new StoreableTableProvider(tmpFolder.getRoot().getPath());
        outputStream = new ByteArrayOutputStream();
        outputStreamError = new ByteArrayOutputStream();
        printStream = new PrintStream(outputStream);
        printStreamError = new PrintStream(outputStreamError);
    }

    @Test(expected = IllegalArgumentException.class)
    public void initializationWithNullArgumentsThrowsException() {
        new Interpreter(null, null, null, null);
    }

    @Test
    public void runInteractiveMode() throws IOException {
        Interpreter interpreter = new Interpreter(new Welcome(), new ByteArrayInputStream(
                (testCommand + commandSeparator + "exit").getBytes()), printStream, printStream);
        interpreter.interactiveMode();
        String actual = outputStream.toString();
        String expected = "$ All is well\r\n";
        assertEquals(actual, expected);
    }

    @Test
    public void runInteractiveModeWithWrongCommand() throws IOException {
        Interpreter interpreter = new Interpreter(new Welcome(), new ByteArrayInputStream(
                ("wtf" + commandSeparator + "exit").getBytes()), printStream, printStream);
        interpreter.interactiveMode();
        String actual = outputStream.toString();
        String expected = "$ Not found command: wtf\r\n";
        assertEquals(actual, expected);
    }

    @Test(expected = IllegalStateException.class)
    public void runInteractiveModeWithWrongNumberOfArguments() throws IOException {
        Interpreter interpreter = new Interpreter(new Welcome(), new ByteArrayInputStream(
                (testCommand + " 1" + commandSeparator + "exit").getBytes()), printStream, printStream);
        interpreter.interactiveMode();
        String actual = outputStream.toString();
        String expected = "Incorrect number of arguments in wtf\r\n";
        assertEquals(actual, expected);
    }

    @Test
    public void runBatchMode() throws IOException {
        String[] args = new String[]{testCommand};
        state = new Welcome(tmpFolder.getRoot().toPath(), args);
        Interpreter interpreter = new Interpreter(
                state, new ByteArrayInputStream(new byte[]{}), printStream, printStream);
        interpreter.batchMode(args);
        String actual = outputStream.toString();
        String expected = "All is well\r\n";
        assertEquals(actual, expected);
    }

    @Test(expected = IllegalStateException.class)
    public void runBathModeWithWrongNumberOfArguments() throws IOException {
        String[] args = new String[]{testCommand + " 1"};
        state = new Welcome(tmpFolder.getRoot().toPath(), args);
    }

    @Test(expected = IllegalStateException.class)
    public void runBatchModeWithWrongCommand() throws IOException {
        String[] args = new String[]{"wtf"};
        state = new Welcome(tmpFolder.getRoot().toPath(), args);
    }

}
